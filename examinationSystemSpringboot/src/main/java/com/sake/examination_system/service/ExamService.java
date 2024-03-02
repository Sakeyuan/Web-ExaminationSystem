package com.sake.examination_system.service;

import com.sake.examination_system.entity.StudentAnswer;
import com.sake.examination_system.util.MyResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface ExamService {

    MyResponseEntity<Object> handleStudentEaxm(StudentAnswer examSubmission, HttpServletRequest request);

    Boolean setExamTime(int userId, int examTotalTime);

    Long getEaxmRemainingTime(int userId);

    void removeEaxmRemainingTime(String userId);

    MyResponseEntity<Object> getGradeDetail(int paperId);
}
