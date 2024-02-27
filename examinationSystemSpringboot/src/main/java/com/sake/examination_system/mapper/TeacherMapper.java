package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.Teacher;

public interface TeacherMapper {
    int addTeacher(Teacher teacher);
    int getTeacherIdByPhone(String userPhone);

    Teacher getTeacherByUserId(int userId);

    int getTeacherIdByUserId(Integer parseAuthorization);
}
