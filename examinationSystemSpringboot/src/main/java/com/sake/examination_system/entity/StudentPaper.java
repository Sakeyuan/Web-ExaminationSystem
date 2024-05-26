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

    public StudentPaper(Integer paperId, int studentId) {
        this.paperId = paperId;
        this.studentId = studentId;
    }

    public StudentPaper() {
        this.paperId = 0;
        this.studentId = 0;
        this.isFinish = false;
        this.isCorrect = false;
        this.scores = 0;
    }
}
