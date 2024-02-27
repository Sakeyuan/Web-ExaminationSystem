package com.sake.examination_system.entity;

import lombok.Data;

import java.util.List;

@Data
public class StudentAnswer {
    private int studentId;
    private int paperId;
    private List<Answer> answers;  // 修正这里的属性名

    @Data
    public static class Answer{
        private int titleId;
        private Object  answer;
    }
}
