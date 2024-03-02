package com.sake.examination_system.service.Imp;

import com.sake.examination_system.entity.DTO.EmailCodeDTO;
import com.sake.examination_system.service.AuthService;
import com.sake.examination_system.service.RedisService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImp implements AuthService {
    @Resource
    RedisService redisService;

    @Override
    public MyResponseEntity<String> refreshToken(HttpServletRequest request,HttpServletResponse response, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        int userId = SakeUtil.parseAuthorization(request);
        String sign = SakeUtil.getSecretKey();
        return new MyResponseEntity<>(HttpServletResponse.SC_OK,"SUCCESS",2, SakeUtil.getToken(userId,sign),SakeUtil.getRefreshToken(userId,sign));
    }

    @Override
    public MyResponseEntity<String> verifyCode(EmailCodeDTO emailCodeDTO) {
        String key = SakeUtil.generateMD5(emailCodeDTO.getEmail());
        if(redisService.hasKey(key)){
            if(redisService.getValue(key).equals(emailCodeDTO.getCode())){
                return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
            }
            else{
                System.out.println("验证码错误");
                return new MyResponseEntity<>(CodeNums.ERROR,"验证码错误");
            }
        }
        else{
            System.out.println("验证码过期");
            return new MyResponseEntity<>(CodeNums.ERROR,"验证码过期，请重新发送");
        }
    }
}
