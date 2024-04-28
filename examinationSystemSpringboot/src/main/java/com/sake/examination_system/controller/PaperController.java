package com.sake.examination_system.controller;

import com.sake.examination_system.entity.DTO.CorrectAnswerDTO;
import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.PaperAddClassDTO;
import com.sake.examination_system.entity.DTO.PaperDTO;
import com.sake.examination_system.entity.ExamRecords;
import com.sake.examination_system.service.PaperService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/paper")
public class PaperController {
    @Resource
    PaperService paperService;

    @PostMapping("/releasePaper")
    public MyResponseEntity<Object> releasePaper(@RequestBody PaperDTO paperDTO) {
        return paperService.releasePaper(paperDTO);
    }

    @PostMapping("/checkRepetition")
    public MyResponseEntity<Object> checkRepetition(@RequestBody PaperDTO paperDTO) {
        return paperService.checkRepetition(paperDTO);
    }

    @PostMapping("/setPaperReleased")
    public MyResponseEntity<Object>setPaperReleased(@RequestBody Integer paperId){
        return paperService.setPaperReleased(paperId);
    }

    @PostMapping("/getAllPaperListByTeacherId")
    public MyResponseEntity<Object> getAllPaperListByTeacherId(@RequestBody PageDTO pageDTO) {
        return paperService.getAllPaperListByTeacherId(pageDTO);
    }

    @DeleteMapping("/deletePaperById/{id}")
    public MyResponseEntity<Object> deletePaperById(@PathVariable int id){
        return paperService.deletePaperById(id);
    }

    @PostMapping("/batchDelById")
    public MyResponseEntity<Object> batchDelById(@RequestBody List<Integer>ids){
        return paperService.batchDelById(ids);
    }

    @PostMapping("/getUnFinishPaperByStudentId")
    public MyResponseEntity<Object> getUnFinishPaperByStudentId(@RequestBody  PageDTO pageDTO){
        return paperService.getPaperByStudentId(pageDTO,false,false);
    }

    @PostMapping("/getFinishPaperByStudentId")
    public MyResponseEntity<Object> getFinishPaperByStudentId(@RequestBody  PageDTO pageDTO){
        return paperService.getPaperByStudentId(pageDTO,true,null);
    }

    @PostMapping("/getNoCorrectPaper")
    public MyResponseEntity<Object> getNoCorrectPaper(@RequestBody  PageDTO pageDTO){
        return paperService.getCorrectPaper(pageDTO,true,false);
    }

    @PostMapping("/getFinishCorrectPaper")
    public MyResponseEntity<Object> getFinishCorrectPaper(@RequestBody  PageDTO pageDTO){
        return paperService.getCorrectPaper(pageDTO,true,true);
    }

    @GetMapping("/getOnePaperWithContentAndAnswer")
    public MyResponseEntity<Object> getOnePaperWithContentAndAnswer(@RequestParam("paperId") int paperId, @RequestParam("studentId") int studentId){
        return paperService.getOnePaperWithContentAndAnswer(paperId,studentId);
    }

    @GetMapping("/getOnePaperWithContentAndScore")
    public MyResponseEntity<Object> getOnePaperWithContentAndScore(@RequestParam("paperId") int paperId, @RequestParam("studentId") int studentId){
        return paperService.getOnePaperWithContentAndScore(paperId,studentId);
    }

    @GetMapping("/getOnePaperWithContentAndScoreAndEmpty")
    public MyResponseEntity<Object> getOnePaperWithContentAndScoreAndEmpty(@RequestParam("paperId") int paperId){
        return paperService.getOnePaperWithContentAndScoreAndEmpty(paperId);
    }

    @PostMapping("/submitCorrectAnswer")
    public MyResponseEntity<Object> submitCorrectAnswer(@RequestBody CorrectAnswerDTO correctAnswerDTO){
        return paperService.submitCorrectAnswer(correctAnswerDTO);
    }

    @GetMapping("/getStudentHomeData/{id}")
    public MyResponseEntity<Object> getStudentHomeData(@PathVariable("id")int id){
        return paperService.getStudentHomeData(id);
    }

    @PostMapping("/addPaperClass")
    public MyResponseEntity<Object> addPaperClass(@RequestBody PaperAddClassDTO paperAddClassDTO){
        return paperService.addPaperClass(paperAddClassDTO);
    }

    @PostMapping("/favorite")
    public MyResponseEntity<Object> favorite(@RequestBody ExamRecords examRecords){
        return paperService.favorite(examRecords);
    }

    @GetMapping("/getFavorite/{studentId}")
    public MyResponseEntity<Object> getFavorite(@PathVariable("studentId") int studentId){
        return paperService.getFavorite(studentId);
    }

    @DeleteMapping("/cancelFavorite/{examId}")
    public MyResponseEntity<Object> cancelFavorite(@PathVariable("examId") int examId){
        return paperService.cancelFavorite(examId);
    }
}
