package com.sake.examination_system.service;

import com.sake.examination_system.entity.DTO.RegisterDTO;
import com.sake.examination_system.util.MyResponseEntity;

public interface RegisterService {
    MyResponseEntity<Object> uploadRegisterInfo(String userPhone);
    MyResponseEntity<Object> studentRegister(RegisterDTO registerDTO, int fileId);
    MyResponseEntity<Object> teacherRegister(RegisterDTO registerDTO);
}
