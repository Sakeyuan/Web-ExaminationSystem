package com.sake.examination_system.service.Imp;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.sake.examination_system.entity.DTO.ExamDTO;
import com.sake.examination_system.entity.DTO.RegisterDTO;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.entity.myFile;
import com.sake.examination_system.exception.ServiceException;
import com.sake.examination_system.mapper.FileMapper;
import com.sake.examination_system.mapper.StudentMapper;
import com.sake.examination_system.service.*;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

@Service
public class FileUploadServiceImp implements FileUploadService {

    @Value("${files.upload.filePath}")
    private String fileUploadDir;

    @Value("${files.upload.faceFilePath}")
    private String faceFilePath;

    @Value("${files.upload.avatarFilePath}")
    private String avatarFilePath;

    @Value("${files.upload.staticFilePath}")
    private String staticFilePath;

    @Value("${files.upload.tmpFaceFilePath}")
    private String tmpFaceFilePath;

    @Resource
    StudentMapper studentMapper;

    @Resource
    FileMapper fileMapper;

    @Resource
    UserService userService;

    @Resource
    RegisterService registerService;

    @Resource
    FaceRecognitionService faceRecognitionService;

    @Resource
    RedisService redisService;

    @Override
    public myFile getFileByMd5(String md5) {
        return fileMapper.getFileByMd5(md5);
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String fileType = FileUtil.extName(originalFileName);
        long fileSize = file.getSize();
        //定义文件标识符
        String fileId = IdUtil.fastSimpleUUID();
        String fileUid = fileId + "." + fileType;
        //如果不存在相同MD5值的文件，进行文件上传
        File uploadDir =  new File(fileUploadDir);
        File uploadFile = new File(fileUploadDir + fileUid);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        //先存储到磁盘
        file.transferTo(uploadFile);
        String fileMd5 = SakeUtil.getFileMD5Code(fileUploadDir + fileUid);
        String fileUrl = null;

        //检查数据库中是否存在相同MD5值的文件
        myFile dbFile = getFileByMd5(fileMd5);
        if (dbFile != null) {
            fileUrl = dbFile.getFileUrl();
            uploadFile.delete();        //删除上传的文件
        }
        else{
            fileUrl =  "/files/" + fileUid;
        }
        //存储到数据库
        UserServiceImp.saveFileToDB(originalFileName, fileType, fileSize, fileMd5, fileUrl, fileMapper);
        return fileUrl;
    }

    @Transactional
    @Override
    public MyResponseEntity<Object> uploadFace(MultipartFile file, RegisterDTO registerDTO) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String fileType = FileUtil.extName(originalFileName);
        long fileSize = file.getSize();
        //定义文件标识符
        String fileUid = registerDTO.getNumber() + "." + fileType;
        //如果不存在相同MD5值的文件，进行文件上传
        File uploadDir =  new File(faceFilePath);
        String fileUuid = faceFilePath + fileUid;

        File uploadFile = new File(fileUuid);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        //先存储到磁盘
        file.transferTo(uploadFile);

        //检测人脸
        Boolean isHaveFace = faceRecognitionService.recognizeFace(fileUuid);
        if(!isHaveFace){
            uploadFile.delete();
            return new MyResponseEntity<>(CodeNums.ERROR,"无法检测到人脸，请重新拍照");
        }

        String fileMd5 = SakeUtil.getFileMD5Code(fileUuid);
        String fileUrl = null;

        //检查数据库中是否存在相同MD5值的文件
        myFile dbFile = getFileByMd5(fileMd5);
        if (dbFile != null) {
            fileUrl = dbFile.getFileUrl();
            uploadFile.delete();        //删除上传的文件
        }
        else{
            fileUrl =  "/files/faces/" + fileUid;
        }
        //存储到数据库
        myFile sqlFile = new myFile();
        sqlFile.setFileName(registerDTO.getNumber());
        sqlFile.setFileType(fileType);
        sqlFile.setFileSize(fileSize/1024);
        sqlFile.setFileUrl(fileUrl);
        sqlFile.setFileMd5(fileMd5);
        fileMapper.addFile(sqlFile);
        MyResponseEntity<Object> r = registerService.studentRegister(registerDTO,sqlFile.getFileId());
        if(r.getCode() == 5000){
            uploadFile.delete();
        }
        return r;
    }

    @Override
    public MyResponseEntity<Object> uploadAvatar(MultipartFile file, User user) throws Exception {
        return null;
    }

    @Override
    public void download(String fileUid, HttpServletResponse response) {
        // 构建文件完整路径
        String filePath = fileUploadDir + fileUid;

        // 检查文件是否存在
        File file = new File(filePath);
        if (!file.exists()) {
            // 文件不存在，返回404状态码
            return;
        }

        // 设置响应头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileUid);

        // 返回文件资源
        Path path = Paths.get(filePath);
        FileSystemResource fileSystemResource = new FileSystemResource(path);
    }

    @Override
    public void download2(String fileUid, HttpServletResponse response) throws IOException {
        //获取文件
        File uploadFile = new File(fileUploadDir + fileUid);

        //设置输出流
        ServletOutputStream out =  response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileUid,"UTF-8"));
        response.setContentType("application/octet-stream");
        //读取文件字节流
        out.write(FileUtil.readBytes(uploadFile));
        out.flush();
        out.close();
    }

    @Override
    public MyResponseEntity<String> getFace(String fileName) throws FileNotFoundException {
        return getImgFile(fileName, faceFilePath);
    }

    @Override
    public MyResponseEntity<String> getAvatar(String fileName) throws IOException {
        return getImgFile(fileName, avatarFilePath);
    }

    @Override
    public MyResponseEntity<String> getAvatarByToken(HttpServletRequest httpServletRequest) {
        Integer userId = SakeUtil.parseAuthorization(httpServletRequest);
        String fileUrl =  userService.getAvatarByUserId(userId);
        return getImgFile(fileUrl,staticFilePath);
    }

    @Transactional
    @Override
    public MyResponseEntity<Object> handlePersonalInfo(MultipartFile file, User user,HttpServletRequest request) throws Exception {
        int userAvatarId = 0;
        if (file != null && !file.isEmpty()) {
            userAvatarId = userService.uploadAvatar(file);
        }
        if(userAvatarId != 0){
            user.setUserAvatarId(userAvatarId);
        }
        userService.updateUserInfo(user);
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS",0,userService.getPersonalInfo(request).getData(),getAvatarByToken(request));
    }

    @Override
    public MyResponseEntity<Object> faceRecognition(MultipartFile file, ExamDTO examDTO) throws Exception {
        File uploadDir = new File(tmpFaceFilePath);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        String originalFileName = file.getOriginalFilename();
        String fileType = FileUtil.extName(originalFileName);
        String fileUuid = tmpFaceFilePath + originalFileName;;
        File uploadFile = new File(fileUuid);
        file.transferTo(uploadFile);
        if(!faceRecognitionService.recognizeFace(fileUuid)){
            uploadFile.delete();
            return new MyResponseEntity<>(CodeNums.ERROR,"没有检测到人脸");
        }
        String studentFacePath = staticFilePath +  studentMapper.getFaceurlById(examDTO.getId());
        Boolean isRight = faceRecognitionService.compareFaces(fileUuid,studentFacePath);
        if(isRight){
            uploadFile.delete();
            return new MyResponseEntity<>(CodeNums.SUCCESS,"比对成功");
        }
        return new MyResponseEntity<>(CodeNums.ERROR,"失败");
    }

    private MyResponseEntity<String>getImgFile(String fileName, String filePath) {
        try {
            Path imagePath = Paths.get(filePath, fileName);
            byte[] imageBytes = Files.readAllBytes(imagePath);
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
            return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS",base64Image);
        } catch (Exception e) {
            throw new ServiceException(CodeNums.SUCCESS,"没有设置头像");
        }
    }
}
