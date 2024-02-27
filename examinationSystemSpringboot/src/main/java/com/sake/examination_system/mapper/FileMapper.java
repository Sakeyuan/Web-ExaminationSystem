package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.myFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    int addFile(myFile file);
    myFile getFileByMd5(String md5);

    myFile getFileById(int id);
}
