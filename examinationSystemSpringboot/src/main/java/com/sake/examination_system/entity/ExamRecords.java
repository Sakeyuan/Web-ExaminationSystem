package com.sake.examination_system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ExamRecords {
    private int examId;
    private int studentId;
    private int paperId;
    private int titleId;
    private String answer;
    @TableField("is_correct")
    private boolean isCorrect;
    private int scores;
    @TableField("is_favorite")
    private Boolean isFavorite;
}
