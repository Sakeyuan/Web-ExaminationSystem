package com.sake.examination_system.service.Imp;

import com.sake.examination_system.service.ClassService;
import com.sake.examination_system.service.StudentService;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/video-stream/student/{studentId}")
public class WebSocketVideoStudent {
    private static ClassService classService;
    private static StudentService studentService;
    private static RedisServiceImp redisServiceImp;

    @Autowired
    public void setClassService(ClassService service){
        classService = service;
    }
    @Autowired
    public void setRedisServiceImp(RedisServiceImp redisService){
        redisServiceImp = redisService;
    }

    @Autowired
    public void setStudentService(StudentService service){
        studentService = service;
    }

    private Session session;
    private String studentId;
    private String teacherId;

    public static Map<String, Set<Session>> teacherToStudent = new ConcurrentHashMap<>();
    public static Map<String, Session> studentIdToSession = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("studentId") String studentId) {
        this.session = session;
        this.studentId = studentId;
        System.out.println("studentId： " + studentId );
        String teacherId = classService.getTeacherIdById(studentService.getClassIdById(Integer.valueOf(studentId))).toString();
        System.out.println("teacherId： " + teacherId );
        this.teacherId = teacherId;
        studentIdToSession.put(studentId,session);
        addStudentToTeacher(teacherId,studentId);
    }

    private void addStudentToTeacher(String teacherId, String studentId) {
        String key = teacherId + SakeUtil.TEACHER_KEY;
        if (redisServiceImp.hasKey(key)){
            String studentIds = (String) redisServiceImp.getValue(key);
            Set<String> studentIdsSet = new HashSet<>(Arrays.asList(studentIds.split(",")));
            studentIdsSet.add(studentId);
            redisServiceImp.setValue(key,String.join(",", studentIdsSet));
        }
        else{
            // 如果键不存在，创建一个新的键，并将 studentId 添加到新的 Set 中
            Set<String> studentIdsSet = new HashSet<>();
            studentIdsSet.add(studentId);
            redisServiceImp.setValue(key, String.join(",", studentIdsSet));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Session teacherSession = WebSocketVideoTeacher.teacherSessionsMaps.get(this.teacherId);
        teacherSession.getAsyncRemote().sendText(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // 从学生到教师的映射中移除该学生
        if (teacherToStudent.containsKey(teacherId)) {
            teacherToStudent.get(teacherId).remove(session);
        }
        studentIdToSession.remove(studentId);
        cleanRedisResources();
    }

    private void cleanRedisResources() {
        try {
            String key = teacherId + SakeUtil.TEACHER_KEY;
            if (redisServiceImp.hasKey(key)) {
                String studentIds = (String) redisServiceImp.getValue(key);
                Set<String> studentIdsSet = new HashSet<>(Arrays.asList(studentIds.split(",")));
                studentIdsSet.remove(this.studentId);
                System.out.println("移除学生： " + this.studentId);
                if (studentIdsSet.isEmpty()) {
                    redisServiceImp.deleteValue(key); // 如果没有学生了，删除键
                } else {
                    redisServiceImp.setValue(key, String.join(",", studentIdsSet)); // 否则更新键的值
                }
            }
        } catch (Exception e) {
            System.err.println("清理 Redis 资源时出现异常：" + e.getMessage());
        }
    }


    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
