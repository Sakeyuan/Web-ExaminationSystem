package com.sake.examination_system.entity;

import lombok.Data;

@Data
public class Class {
    private int classId;
    private String className;
    private String classCode;
    private int studentNumbers;
    private int teacherId;
}
