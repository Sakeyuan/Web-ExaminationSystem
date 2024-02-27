package com.sake.examination_system.controller;

import com.sake.examination_system.entity.DTO.InviteDTO;
import com.sake.examination_system.entity.DTO.SingleInviteDTO;
import com.sake.examination_system.service.TeacherService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    TeacherService teacherService;

    @GetMapping("/exportStudentInfo")
    public void export(HttpServletResponse httpServletResponse,@RequestParam("id") int id) throws IOException {
        teacherService.exportInfo(httpServletResponse,id);
    }

    @PostMapping("/importStudentInfo")
    public MyResponseEntity<Object> importStudent(MultipartFile file) throws IOException {
        return teacherService.importStudent(file);
    }

    @PostMapping("/invite")
    public MyResponseEntity<Object> inviteStudent(@RequestBody InviteDTO studentList){
        return teacherService.inviteStudent(studentList);
    }

    @PostMapping("/singleInvite")
    public MyResponseEntity<Object> singleInvite(@RequestBody SingleInviteDTO studentInfo){
        return teacherService.singleInvite(studentInfo);
    }

    @GetMapping("/getHomeData")
    public MyResponseEntity<Object>getHomeData(HttpServletRequest request){
        return teacherService.getHomeData(request);
    }

}
