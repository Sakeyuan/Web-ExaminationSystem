package com.sake.examination_system.entity.DTO;
import java.util.Map;

import lombok.Data;

@Data
public class CorrectAnswerDTO {
    private int paperId;
    private int studentId;
    private int totalScore;
    private Map<String, Integer> correctResult;
}
