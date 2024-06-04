package com.sake.examination_system.controller;
import com.sake.examination_system.entity.DTO.EmailCodeDTO;
import com.sake.examination_system.service.AuthService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    AuthService authService;


    @PostMapping("/refreshToken")
    public MyResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response, @RequestBody String refreshToken){
        return authService.refreshToken(request,response,refreshToken);
    }

    @PostMapping("/verifyCode")
    public MyResponseEntity<String> verifyCode(@RequestBody EmailCodeDTO emailCodeDTO){
        return authService.verifyCode(emailCodeDTO);
    }
}
