package com.sake.examination_system.entity;


import lombok.Data;

@Data
public class Student {
    //学生ID
    private int studentId;

    //学生学号
    private String  studentNumber;

    //学生人脸
    private int studentFaceId;

    //用户信息
    private User user;

    //班级信息
    private Class myClass;

    public int getUserId() {
        return user.getUserId();
    }
}
