package com.sake.examination_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sake.examination_system.entity.DTO.ExamDTO;
import com.sake.examination_system.entity.DTO.RegisterDTO;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.service.FileUploadService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件上传接口
 */
@RestController
@RequestMapping("/file")
public class uploadFileController {

    @Resource
    FileUploadService fileUploadService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws Exception {
        return fileUploadService.upload(file);
    }

    @ResponseBody
    @RequestMapping(value = "/uploadFace", method = RequestMethod.POST, consumes = "multipart/form-data")
    public MyResponseEntity<Object> uploadFace(@RequestPart("file") MultipartFile file, @RequestPart("data") RegisterDTO registerDTO) throws Exception {
        return fileUploadService.uploadFace(file,registerDTO);
    }

    @GetMapping("/download/{fileUid}")
    public void download(@PathVariable String fileUid, HttpServletResponse response) throws IOException {
        fileUploadService.download(fileUid,response);
    }

    @GetMapping("/getFace/{fileName}")
    public MyResponseEntity<String> getFace(@PathVariable String fileName) throws IOException {
        return fileUploadService.getFace(fileName);
    }

    @GetMapping("/getAvatar/{fileName}")
    public MyResponseEntity<String> getAvatar(@PathVariable String fileName) throws IOException {
        return fileUploadService.getAvatar(fileName);
    }

    @GetMapping("/getAvatar")
    public MyResponseEntity<String> getAvatarByToken(HttpServletRequest httpServletRequest)  {
        return fileUploadService.getAvatarByToken(httpServletRequest);
    }

    @GetMapping("/fileUid")
    public void download2(@PathVariable String fileUid, HttpServletResponse httpServletResponse) throws IOException {
        fileUploadService.download2(fileUid,httpServletResponse);
    }

    @RequestMapping(value = "/uploadPersonalInfo", method = RequestMethod.POST, consumes = "multipart/form-data")
    public MyResponseEntity<Object> uploadPersonalInfo(@RequestPart(name = "file", required = false) MultipartFile file,
                                      @RequestPart(name = "user", required = false) User user,HttpServletRequest request) throws Exception {
        return fileUploadService.handlePersonalInfo(file,user,request);
    }

    @ResponseBody
    @RequestMapping(value = "/faceRecognition", method = RequestMethod.POST, consumes = "multipart/form-data")
    public MyResponseEntity<Object> faceRecognition(@RequestPart("file") MultipartFile file, @RequestPart("data") ExamDTO examDTO) throws Exception {
        return fileUploadService.faceRecognition(file,examDTO);
    }
}
