package com.sake.examination_system.controller;

import com.sake.examination_system.entity.StudentAnswer;
import com.sake.examination_system.service.ExamService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/exam")
public class ExamController {
    @Resource
    ExamService examService;

    @PostMapping("/submitExam")
    public MyResponseEntity<Object> submitExam(@RequestBody StudentAnswer examSubmission, HttpServletRequest request) {
        return examService.handleStudentEaxm(examSubmission,request);
    }

    @GetMapping("/getGradeDetail/{paperId}")
    public MyResponseEntity<Object> getGradeDetail(@PathVariable int paperId) {
        return examService.getGradeDetail(paperId);
    }
}
