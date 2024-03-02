package com.sake.examination_system.service;

import com.sake.examination_system.entity.DTO.EmailCodeDTO;
import com.sake.examination_system.util.MyResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    MyResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken);

    MyResponseEntity<String> verifyCode(EmailCodeDTO emailCodeDTO);
}
