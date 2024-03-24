package com.sake.examination_system.service.Imp;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/video-stream/teacher/{userId}")
public class WebSocketVideoTeacher {


    private static final Map<String, Session> teacherSessions = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        System.out.println(userId + ":  教师连接");
        teacherSessions.put(userId, session);
        
    }

    @OnClose
    public void onClose(Session session) {
        // 移除关闭的教师连接
        String userId = getUserId(session);
        if (userId != null) {
            teacherSessions.remove(userId);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private String getUserId(Session session) {
        for (Map.Entry<String, Session> entry : teacherSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void sendVideoData(String studentUserId,String teacherId, byte[] videoData) {
        System.out.println("sendVideoData: " + studentUserId + " To " + teacherId);
        Session teacherSession = teacherSessions.get(teacherId);
        if (teacherSession.isOpen()) {
            System.out.println(teacherId + ":  发送给学生端");
            teacherSession.getAsyncRemote().sendBinary(ByteBuffer.wrap(videoData));
        }
    }
}
