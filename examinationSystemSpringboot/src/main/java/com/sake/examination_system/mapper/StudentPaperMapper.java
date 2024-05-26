package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.PaperWithStudentDTO;
import com.sake.examination_system.entity.StudentPaper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentPaperMapper {
    Integer addPaper(StudentPaper studentPaper);

    List<Integer> getPaperIdByStudent(int id,boolean isFinish,Boolean isCorrect);

    Integer getPaperIdByStudentTotal(int id,boolean isFinish,Boolean isCorrect);

    Integer isFinishPaper(int studentId, int paperId);

    List<PaperWithStudentDTO>getCorrectPaper(PageDTO pageDTO, boolean isFinish, Boolean isCorrect);

    Integer getCorrectPaperTotal(PageDTO pageDTO,boolean isFinish, Boolean isCorrect);

    void submitCorrect(int paperId, int studentId, int totalScore);

    StudentPaper getExamRecordsByPidAndSid(int paperId, int studentId);

    int getCountsStudentPaper(int id,Boolean isFinish,Boolean isCorrect);

    Boolean getPaperIsFinish(int studentId, int paperId);

    Integer getClassScoreAvg(List<Integer> studentIdList,int paperId);

    void updateSpendTime(int paperId, int studentId, Long spendTime);

    void removePaperByStudentId(int studentId);
}
