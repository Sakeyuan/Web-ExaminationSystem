package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.DTO.LoginDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {
    Integer loginCheck(LoginDTO loginDTO);
    int getRole(String userPhone);
}
