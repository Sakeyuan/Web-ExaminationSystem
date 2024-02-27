package com.sake.examination_system.service;

import com.sake.examination_system.entity.StudentAnswer;
import com.sake.examination_system.util.MyResponseEntity;

public interface ExamService {

    MyResponseEntity<Object> handleStudentEaxm(StudentAnswer examSubmission);

    Boolean setExamTime(int userId, int examTotalTime);

    Long getEaxmRemainingTime(int userId);

    void removeEaxmRemainingTime(String userId);
}
