package com.sake.examination_system.entity;
import lombok.Data;

@Data
public class StudentPaper {
    private int studentPaperId;
    private int studentId;
    private int paperId;
    private boolean isFinish;
    private boolean isCorrect;
    private int scores;
}
