package com.sake.examination_system.service;

import com.sake.examination_system.entity.DTO.ExamDTO;
import com.sake.examination_system.entity.DTO.RegisterDTO;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.entity.myFile;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileUploadService {
    /**
     * 通过文件的md5查询文件
     * @param md5
     * @return
     */
    myFile getFileByMd5(String md5);

    String upload(MultipartFile file) throws Exception;

    MyResponseEntity<Object> uploadFace(MultipartFile file, RegisterDTO registerDTO) throws Exception;

    MyResponseEntity<Object> uploadAvatar(MultipartFile file, User user) throws Exception;

    void download(String fileUid, HttpServletResponse response);

    void download2(String fileUid, HttpServletResponse response) throws IOException;

    MyResponseEntity<String>getFace(String fileName) throws IOException;

    MyResponseEntity<String> getAvatar(String fileName) throws IOException;


    MyResponseEntity<String>getAvatarByToken(HttpServletRequest httpServletRequest);

    MyResponseEntity<Object> handlePersonalInfo(MultipartFile file, User user,HttpServletRequest request) throws Exception;

    MyResponseEntity<Object> faceRecognition(MultipartFile file, ExamDTO examDTO) throws Exception;
}
