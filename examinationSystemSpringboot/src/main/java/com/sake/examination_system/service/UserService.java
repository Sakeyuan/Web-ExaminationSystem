package com.sake.examination_system.service;

import com.sake.examination_system.entity.DTO.EmailCodeDTO;
import com.sake.examination_system.entity.Student;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    MyResponseEntity<Integer> addUser(User user);
    MyResponseEntity<List<Student>> getPage(int pageNum, int pageSize, int id, String userRealName);
    MyResponseEntity<Object> getPersonalInfo(HttpServletRequest request);
    User getUserById(int userId);

    String getAvatarByUserId(int parseInt);

    int uploadAvatar(MultipartFile file) throws Exception;

    void updateUserInfo(User user);

    MyResponseEntity<Object> sendVerificationCode(String email);

    MyResponseEntity<Object> setPassword(EmailCodeDTO emailCodeDTO);
}
