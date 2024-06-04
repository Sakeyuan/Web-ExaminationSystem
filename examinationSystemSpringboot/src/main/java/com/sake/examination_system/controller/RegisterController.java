package com.sake.examination_system.controller;

import com.sake.examination_system.entity.DTO.RegisterDTO;
import com.sake.examination_system.service.RegisterService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/register")
public class RegisterController {
    @Resource
    RegisterService registerService;

    @PostMapping("/uploadRegisterInfo")
    public MyResponseEntity<Object> uploadRegisterInfo(@RequestBody String userPhone){
        return registerService.uploadRegisterInfo(userPhone);
    }

    @PostMapping("/teacherRegister")
    public MyResponseEntity<Object> teacherRegister(@RequestBody RegisterDTO registerDTO){
        return registerService.teacherRegister(registerDTO);
    }
}
