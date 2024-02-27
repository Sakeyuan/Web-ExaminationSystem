package com.sake.examination_system.controller;

import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.TitleDTO;
import com.sake.examination_system.service.TitleService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/title")
public class TitleController {
    @Resource
    TitleService titleService;

    @PostMapping("/getAllTitlesByTeacherId")
    public MyResponseEntity<Object> getAllTitlesByTeacherId(@RequestBody PageDTO pageDTO){
        return titleService.getAllTitlesByTeacherId(pageDTO);
    }

    @PostMapping("/createTitle")
    public MyResponseEntity<Object> createTitle(@RequestBody TitleDTO titleDTO){
        return titleService.createTitle(titleDTO);
    }

    @DeleteMapping("/deleteTitleById/{id}")
    public MyResponseEntity<Object> deleteTitleById(@PathVariable("id") int id){
        return titleService.deleteTitleById(id);
    }

    @PostMapping("/batchDeleteTitleById")
    public MyResponseEntity<Object> batchDeleteTitleById(@RequestBody List<Integer> titleIds){
        return titleService.batchDeleteTitleById(titleIds);
    }

    @PostMapping("/getTitleById")
    public MyResponseEntity<Object> getTitleById(@RequestBody List<Integer>idList){
        return titleService.getTitleById(idList);
    }

    @GetMapping("/getAllTitleType")
    public MyResponseEntity<Object> getAllTitleType(){
        return titleService.getAllTitleType();
    }
}
