package com.sake.examination_system.controller;

import com.sake.examination_system.entity.Class;
import com.sake.examination_system.entity.DTO.ClassDTO;
import com.sake.examination_system.service.ClassService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/class")
public class ClassController {

    @Resource
    ClassService classService;

    @GetMapping("/getAllClassByIdPage")
    public MyResponseEntity<List<Class>> getAllClassByIdPage(@RequestParam int pageNum, @RequestParam int pageSize, @RequestParam(required = false) String className, @RequestParam int id){
        return classService.getAllClassByIdPage(pageNum,pageSize,className,id);
    }

    @DeleteMapping("/removeClass/{classId}")
    public MyResponseEntity<Object> removeClass(@PathVariable("classId") int classId){
        return classService.removeClass(classId);
    }

    @PostMapping("/batchRemoveClass")
    public MyResponseEntity<Object> batchRemoveClass(@RequestBody List<Integer> classIds){
        return classService.batchRemoveClass(classIds);
    }

    @PostMapping("/createClassByName")
    public MyResponseEntity<Object> createClassByName(@RequestBody ClassDTO classDTO,HttpServletRequest request){
        return classService.createClassByName(classDTO.getClassName(),request);
    }

    @GetMapping("/getAllClassByTeacherId/{id}")
    public MyResponseEntity<Object> getAllClassByTeacherId(@PathVariable("id")int id){
        return classService.getAllClassByTeacherId(id);
    }

    @PostMapping("/getClassByIds")
    public MyResponseEntity<Object> getClassByIds(@RequestBody Map<String, String> requestBody){
        String paperForClass = requestBody.get("paperForClass");
        return classService.getClassByIds(paperForClass);
    }

    @GetMapping("/getAllClassByTid/{id}")
    public MyResponseEntity<Object> getAllClassByTid(@PathVariable("id")int id){
        return classService.getAllClassByTid(id);
    }

    @GetMapping("/getClassByToken")
    public MyResponseEntity<Object> getClassByToken(HttpServletRequest request){
        return classService.getClassByToken(request);
    }

    @PostMapping("/addClass")
    public MyResponseEntity<Object> addClass(HttpServletRequest request,@RequestBody ClassDTO classDTO){
        return classService.addClass(request,classDTO);
    }

    @DeleteMapping("/bowOutClass")
    public MyResponseEntity<Object> bowOutClass(HttpServletRequest request){
        return classService.bowOutClass(request);
    }
}
