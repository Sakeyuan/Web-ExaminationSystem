package com.sake.examination_system.service.Imp;
import com.sake.examination_system.service.ClassService;
import com.sake.examination_system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/video-stream/student/{userId}")
public class WebSocketVideoStudent {
    private static ClassService classService;
    private static StudentService studentService;

    @Autowired
    public void setClassService(ClassService service){
        classService = service;
    }

    @Autowired
    public void setStudentService(StudentService service){
        studentService = service;
    }

    private static final Map<Session, String> sessionToUserId = new ConcurrentHashMap<>();
    private static final Map<String,Integer> studentToTeacher = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessionToUserId.put(session, userId);
        System.out.println("userId： " + userId );
        Integer teacherId = classService.getTeacherIdById(studentService.getClassIdById(Integer.valueOf(userId)));
        System.out.println("teacherId： " + teacherId );
        studentToTeacher.put(userId,teacherId);
    }

    @OnMessage
    public void onMessage(byte[] videoData, Session session) {
        // 获取学生的userId
        String userId = getUserId(session);
        if (userId != null) {
            WebSocketVideoTeacher.sendVideoData(userId, String.valueOf(studentToTeacher.get(userId)),videoData);
        }
    }

    @OnClose
    public void onClose(Session session) {
        // 移除关闭的学生连接
        String userId = sessionToUserId.get(session);
        if (userId != null) {
            sessionToUserId.remove(session);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private String getUserId(Session session) {
        return sessionToUserId.get(session);
    }
}
