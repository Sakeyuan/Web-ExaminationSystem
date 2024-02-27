package com.sake.examination_system.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegisterMapper {
    int checkUserPhone(String phone);
}
