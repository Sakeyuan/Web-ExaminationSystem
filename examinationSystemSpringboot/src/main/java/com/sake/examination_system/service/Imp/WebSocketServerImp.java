package com.sake.examination_system.service.Imp;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sake.examination_system.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.*;


@Component
@ServerEndpoint("/websocketSendText/{userId}")
public class WebSocketServerImp  {
    private static ExamService examService;

    @Autowired
    public void setExamService(ExamService exam) {
        WebSocketServerImp.examService = exam;
    }

    private Session session;
    private String userId;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //  注：底下WebSocket是当前类名
    private static CopyOnWriteArraySet<WebSocketServerImp> webSockets = new CopyOnWriteArraySet<>();
    // 用来存在线连接用户信息
    private static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();
    private ConcurrentHashMap<String, ScheduledFuture<?>> countdownTasks = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId") String userId) {
        try {
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            sessionPool.put(userId, session);
            System.out.println("【websocket消息】有新的连接，总数为:" + webSockets.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // 使用 Gson 的 JsonParser 解析 JSON 字符串为 JsonObject
            JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
            String command = jsonMessage.get("command").getAsString();
            switch (command) {
                case "uploadExamTotalTime": {
                    JsonObject data = jsonMessage.getAsJsonObject("data");
                    // 检查是否包含 "examTotalTime" 键
                    if (data.has("examTotalTime") && !data.get("examTotalTime").isJsonNull()) {
                        int userId = data.get("userId").getAsInt();
                        int examTotalTime = data.get("examTotalTime").getAsInt();
                        String userIdString = String.valueOf(userId);
                        if(examService.setExamTime(userId, examTotalTime)){
                            ScheduledFuture<?> countdownTask = startCountdownTask(userIdString);
                            countdownTasks.put(userIdString, countdownTask);
                        }
                        else{
                            sendRemainTime(String.valueOf(userId));
                        }
                    }
                    else {
                        // 处理 "examTotalTime" 为空的情况
                        System.out.println("上传的消息中缺少有效的 examTotalTime 值");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRemainTime(String userId){
        Long remainingTime = examService.getEaxmRemainingTime(Integer.parseInt(userId));
        if (remainingTime <= 0) {
            cancelCountdownTask(userId);
        }
        JsonObject message = new JsonObject();
        message.addProperty("type", "uploadRemainingTime");

        JsonObject data = new JsonObject();
        data.addProperty("remainingTime", remainingTime);

        message.add("data", data);
        sendOneMessage(userId, message.toString());
    }

    // 启动倒计时任务
    private ScheduledFuture<?> startCountdownTask(String userId) {
        // 创建并返回一个定时任务，每秒减少倒计时，并在倒计时为0时执行相应操作
        return scheduler.scheduleAtFixedRate(() -> {
            // 处理倒计时逻辑
            sendRemainTime(userId);
        }, 0, 60, TimeUnit.SECONDS);
    }

    // 取消倒计时任务
    private void cancelCountdownTask(String userId) {
        ScheduledFuture<?> countdownTask = countdownTasks.get(userId);
        if (countdownTask != null && !countdownTask.isCancelled()) {
            countdownTask.cancel(true);
        }
        countdownTasks.remove(userId);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        try {
            webSockets.remove(this);
            sessionPool.remove(this.userId);
            cancelCountdownTask(this.userId);
            System.out.println("【websocket消息】连接断开，总数为:"+webSockets.size());
        } catch (Exception e) {
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendAllMessage(String message) {
        for(WebSocketServerImp webSocket : webSockets) {
            try {
                if(webSocket.session.isOpen()) {
                    webSocket.session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息(多人)
    public void sendMoreMessage(String[] userIds, String message) {
        for(String userId:userIds) {
            Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
