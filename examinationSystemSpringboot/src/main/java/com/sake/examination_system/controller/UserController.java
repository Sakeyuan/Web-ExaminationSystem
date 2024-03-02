package com.sake.examination_system.controller;

import com.sake.examination_system.entity.*;
import com.sake.examination_system.entity.DTO.EmailCodeDTO;
import com.sake.examination_system.service.UserService;
import com.sake.examination_system.util.MyResponseEntity;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @PostMapping("/insertUser")
    public MyResponseEntity<Integer> insertUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @GetMapping("/getPersonalInfo")
    public MyResponseEntity<Object> getPersonalInfo(HttpServletRequest request){
        return userService.getPersonalInfo(request);
    }

    @PostMapping("/sendVerificationCode")
    public MyResponseEntity<Object> sendVerificationCode(@RequestBody EmailCodeDTO emailCodeDTO){
        return userService.sendVerificationCode(emailCodeDTO.getEmail());
    }

    @PostMapping("/setPassword")
    public MyResponseEntity<Object> setPassword(@RequestBody EmailCodeDTO emailCodeDTO){
        return userService.setPassword(emailCodeDTO);
    }
}
