package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.ExamRecords;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamRecordsMapper {

    Integer addStudentAnswer(int studentId, int paperId, int titleId,String answerString);

    List<ExamRecords>getRecordsByStudentIdAndPaperId(int paperId,int studentId);

    void updateRecord(ExamRecords studentRecord, int paperId, int studentId, int titleId);

    void submitCorrectAnswer(int paperId, int studentId, String titleId, Integer scores);
}
