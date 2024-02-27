package com.sake.examination_system.service.Imp;

import com.sake.examination_system.service.AuthService;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImp implements AuthService {

    @Override
    public MyResponseEntity<String> refreshToken(HttpServletRequest request,HttpServletResponse response, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        int userId = SakeUtil.parseAuthorization(request);
        String sign = SakeUtil.getSecretKey();
        return new MyResponseEntity<>(HttpServletResponse.SC_OK,"SUCCESS",2, SakeUtil.getToken(userId,sign),SakeUtil.getRefreshToken(userId,sign));
    }
}
