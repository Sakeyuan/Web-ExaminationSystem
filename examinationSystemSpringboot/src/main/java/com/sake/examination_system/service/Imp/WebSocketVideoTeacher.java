package com.sake.examination_system.service.Imp;

import com.sake.examination_system.util.SakeUtil;
import com.sake.examination_system.util.WebRTCData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/video-stream/teacher/{teacherId}")
public class WebSocketVideoTeacher {
    private Session session;
    private String teacherId;
    public static Map<String, Session> teacherSessionsMaps = new ConcurrentHashMap<>();
    private static RedisServiceImp redisServiceImp;

    @Autowired
    public void setRedisServiceImp(RedisServiceImp redisService) {
        redisServiceImp = redisService;
    }

    @OnMessage
    public void onMessage(String message, Session session)  {
        sendToStudent(message);
    }

    private void sendToStudent(String message){
        String key = this.teacherId + SakeUtil.TEACHER_KEY;
        try {
            if (redisServiceImp.hasKey(key)) {
                WebRTCData webRTCData = WebRTCData.fromJson(message);
                System.out.println("学生Id: " + webRTCData.getId());
                Session studentSession = WebSocketVideoStudent.studentIdToSession.get(webRTCData.getId());
                if (studentSession != null && studentSession.isOpen()) {
                    RemoteEndpoint.Async asyncRemote = studentSession.getAsyncRemote();
                    synchronized (asyncRemote) {
                        asyncRemote.sendText(message);
                    }
                } else {
                    System.out.println("Student session does not exist or is closed");
                }
            } else {
                System.out.println("键不存在: " + key);
            }
        } catch (Exception e) {
            System.err.println("发送消息给学生时出现异常：" + e.getMessage());
        }
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("teacherId") String teacherId) {
        this.session = session;
        this.teacherId = teacherId;
        teacherSessionsMaps.put(teacherId, session);
        System.out.println(teacherId + ":  教师连接");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        teacherSessionsMaps.remove(teacherId); // 从映射中移除关闭的会话
        // 不需要在这里关闭会话，因为会话已经在调用 onClose 方法时自动关闭
        // this.session.close();
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
