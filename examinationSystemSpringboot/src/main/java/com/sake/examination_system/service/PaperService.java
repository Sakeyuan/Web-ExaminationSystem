package com.sake.examination_system.service;

import com.sake.examination_system.entity.DTO.CorrectAnswerDTO;
import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.PaperAddClassDTO;
import com.sake.examination_system.entity.DTO.PaperDTO;
import com.sake.examination_system.entity.ExamRecords;
import com.sake.examination_system.util.MyResponseEntity;

import java.util.List;

public interface PaperService {
    MyResponseEntity<Object> releasePaper(PaperDTO paperDTO);

    MyResponseEntity<Object> getAllPaperListByTeacherId(PageDTO pageDTO);

    MyResponseEntity<Object> deletePaperById(int id);

    MyResponseEntity<Object> batchDelById(List<Integer> ids);

    MyResponseEntity<Object> getPaperByStudentId(PageDTO pageDTO, boolean isFinish, Boolean isCorrect);

    MyResponseEntity<Object> getCorrectPaper(PageDTO pageDTO, boolean isFinish, Boolean isCorrect);

    MyResponseEntity<Object> getOnePaperWithContentAndAnswer(int paperId, int studentId);

    boolean singleCompareAnswers(String studentAnswer, String standardAnswer);
    boolean multipleCompareAnswers(String studentAnswer, String standardAnswer);
    boolean judgeCompareAnswers(String studentAnswer, String standardAnswer);

    MyResponseEntity<Object> submitCorrectAnswer(CorrectAnswerDTO correctAnswerDTO);

    MyResponseEntity<Object> getOnePaperWithContentAndScore(int paperId, int studentId);

    MyResponseEntity<Object> getStudentHomeData(int id);

    MyResponseEntity<Object> getOnePaperWithContentAndScoreAndEmpty(int paperId);

    MyResponseEntity<Object> addPaperClass(PaperAddClassDTO paperAddClassDTO);

    MyResponseEntity<Object> favorite(ExamRecords examRecords);

    MyResponseEntity<Object> getFavorite(int studentId);

    MyResponseEntity<Object> cancelFavorite(int examId);
}
