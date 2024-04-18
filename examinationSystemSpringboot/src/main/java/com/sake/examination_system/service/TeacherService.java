package com.sake.examination_system.service;
import com.sake.examination_system.entity.DTO.InviteDTO;
import com.sake.examination_system.entity.DTO.SingleInviteDTO;
import com.sake.examination_system.entity.Teacher;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface TeacherService {
    void exportInfo(HttpServletResponse httpServletResponse,int id) throws IOException;
    MyResponseEntity<Object> importStudent(MultipartFile file) throws IOException;
    MyResponseEntity<Object> addTeacher(Teacher teacher);

    MyResponseEntity<Object> inviteStudent(InviteDTO studentList);

    MyResponseEntity<Object> singleInvite(SingleInviteDTO studentInfo);

    MyResponseEntity<Object> getHomeData(HttpServletRequest request);

    void exportClassStudent(HttpServletResponse httpServletResponse, int classId) throws IOException;

    MyResponseEntity<Object> getOnlineStudents(String id);
}
