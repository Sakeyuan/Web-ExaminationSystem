package com.sake.examination_system.service.Imp;

import com.sake.examination_system.entity.DTO.LoginDTO;
import com.sake.examination_system.entity.RoleConverter;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.mapper.LoginMapper;
import com.sake.examination_system.mapper.StudentMapper;
import com.sake.examination_system.mapper.TeacherMapper;
import com.sake.examination_system.mapper.UserMapper;
import com.sake.examination_system.service.LoginService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginServiceImp implements LoginService {

    @Resource
    LoginMapper loginMapper;

    @Resource
    StudentMapper studentMapper;

    @Resource
    TeacherMapper teacherMapper;

    @Resource
    UserMapper userMapper;

    @Override
    public MyResponseEntity<Object> login(LoginDTO loginDTO) {
        MyResponseEntity<Object> r = new MyResponseEntity<Object>();

        if(checkPhoneIsExist(loginDTO.getUserPhone()) == 0){
            return new MyResponseEntity<Object>(CodeNums.ERROR,"用户不存在");
        }
        if(loginMapper.loginCheck(loginDTO) == null){
            return new MyResponseEntity<Object>(CodeNums.ERROR,"密码错误");
        }
        //老师
        String phone = loginDTO.getUserPhone();
        LoginDTO returnLoginDTO = new LoginDTO();
        User user = userMapper.getUserByPhone(phone);
        int userId = user.getUserId();
        returnLoginDTO.setUserId(userId);
        int id = 0;
        String role = RoleConverter.convertRoleToString(user.getUserRole());
        if(role.equals("教师")){
            id = teacherMapper.getTeacherIdByPhone(phone);
        }
        else{
            id = studentMapper.getStudentIdByPhone(phone);
        }
        returnLoginDTO.setRole(role);
        returnLoginDTO.setId(id);
        returnLoginDTO.setToken(SakeUtil.getToken(userId,SakeUtil.getSecretKey()));
        returnLoginDTO.setRefreshToken(SakeUtil.getRefreshToken(userId,SakeUtil.getSecretKey()));
        return new MyResponseEntity<>(CodeNums.SUCCESS,"登录成功", returnLoginDTO);
    }

    @Override
    public int checkPhoneIsExist(String userPhone) {
        return userMapper.checkPhoneIsExist(userPhone);
    }
}
