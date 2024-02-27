package com.sake.examination_system.controller;

import com.sake.examination_system.entity.DTO.LoginDTO;
import com.sake.examination_system.service.LoginService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Resource
    LoginService loginService;

    @PostMapping("/uploadLoginInfo")
    public MyResponseEntity<Object> login(@RequestBody LoginDTO loginDTO){
        return loginService.login(loginDTO);
    }
}
