package com.sake.examination_system.configration.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.exception.ServiceException;
import com.sake.examination_system.mapper.UserMapper;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {
    @Resource
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request);
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        response.setCharacterEncoding("UTF-8");
        // 获取请求头中的 Authorization
        String authorizationHeader = request.getHeader("Authorization");

        // 检查 Authorization 是否存在
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(CodeNums.NO_TOKEN);
            response.getWriter().write("无token,请重新登录");
            return false;
        }

        // 提取 Token
        String token = authorizationHeader.substring(7); // 7 是 "Bearer " 的长度

        // 解析 Token 获取用户 ID
        String userId = null;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            response.setStatus(CodeNums.TOKEN_ERROR);
            response.getWriter().write("token验证失败");
            return false;
        }

        // 检查用户是否存在
        User user = userMapper.getUserById(Integer.parseInt(userId));
        if (user == null) {
            response.setStatus(CodeNums.TOKEN_ERROR);
            response.getWriter().write("用户不存在");
            return false;
        }

        // 验证 Token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SakeUtil.getSecretKey())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTDecodeException j) {
            response.setStatus(CodeNums.TOKEN_ERROR);
            response.getWriter().write("token验证失败");
            return false;
        } catch (TokenExpiredException e) {
            response.setStatus(CodeNums.TOKEN_EXPIRE);
            response.getWriter().write("token已过期，请重新登录");
            return false;
        }
        return true;
    }
}
