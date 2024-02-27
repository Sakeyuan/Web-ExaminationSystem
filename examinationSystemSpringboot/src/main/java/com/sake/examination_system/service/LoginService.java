package com.sake.examination_system.service;

import com.sake.examination_system.entity.DTO.LoginDTO;
import com.sake.examination_system.util.MyResponseEntity;

public interface LoginService {
    MyResponseEntity<Object> login(LoginDTO loginDTO);
    int checkPhoneIsExist(String userPhone);
}
