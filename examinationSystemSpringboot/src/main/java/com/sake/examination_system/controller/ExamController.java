package com.sake.examination_system.controller;

import com.sake.examination_system.entity.StudentAnswer;
import com.sake.examination_system.service.ExamService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/exam")
public class ExamController {
    @Resource
    ExamService examService;

    @PostMapping("/submitExam")
    public MyResponseEntity<Object> submitExam(@RequestBody StudentAnswer examSubmission) {
        return examService.handleStudentEaxm(examSubmission);
    }
}
