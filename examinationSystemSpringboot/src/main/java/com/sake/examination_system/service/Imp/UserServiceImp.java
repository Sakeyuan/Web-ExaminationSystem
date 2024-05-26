package com.sake.examination_system.service.Imp;

import cn.hutool.core.io.FileUtil;
import com.auth0.jwt.JWT;
import com.sake.examination_system.entity.DTO.EmailCodeDTO;
import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.UserWithSTF;
import com.sake.examination_system.entity.Student;
import com.sake.examination_system.entity.Teacher;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.entity.myFile;
import com.sake.examination_system.exception.ServiceException;
import com.sake.examination_system.mapper.*;
import com.sake.examination_system.service.EmailService;
import com.sake.examination_system.service.RedisService;
import com.sake.examination_system.service.StudentService;
import com.sake.examination_system.service.UserService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import com.sun.mail.smtp.SMTPSenderFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Service
public class UserServiceImp implements UserService {
    @Resource
    UserMapper userMapper;

    @Resource
    StudentService studentService;

    @Resource
    ClassMapper classMapper;

    @Resource
    TeacherMapper teacherMapper;

    @Resource
    FileMapper fileMapper;

    @Resource
    EmailService emailService;

    @Resource
    RedisService redisService;

    @Value("${files.upload.avatarFilePath}")
    private String avatarFilePath;

    @Override
    public MyResponseEntity<Integer> addUser(User user) {
        MyResponseEntity<Integer> r = new MyResponseEntity<Integer>();
        int res = userMapper.addUser(user);
        if(res == 1){
            r.ok();
        }
        else{
            r.error();
        }
        return r;
    }

    @Override
    public MyResponseEntity<List<Student>> getPage(PageDTO pageDTO) {
        MyResponseEntity<List<Student>> r = new MyResponseEntity<List<Student>>();
        List<Student> students = null;
        int id = pageDTO.getId();
        String userRealName = pageDTO.getName();
        int pageNum = pageDTO.getPageNum();
        int pageSize = pageDTO.getPageSize();
        if(id == 0){
            return null;
        }
        List<Integer> classIds = classMapper.getClassIdByTeacherId(id);
        if(classIds.isEmpty()){
            return  null;
        }
        if (userRealName.isEmpty()) {
            students = studentService.getPage(pageNum,pageSize,classIds);
            if(students.isEmpty()){
                return  null;
            }
            for (Student student : students) {
                student.getUser().setUserPassword(null); // 删除 userPassword 属性
                student.getUser().setUserRole(-1);        // 设置 userRole 为默认值（或者您认为合适的其他值）
            }
            r.setTotal(studentService.getAllStudentInfoByClassIdTotal(classIds));
        }
        else{  //按照名称查询学生信息
            userRealName = "%" + userRealName + "%";
            students = studentService.getStudentsByUserRealName(pageNum,pageSize,userRealName,classIds);
            if(students.isEmpty()){
                return  null;
            }
            for (Student student : students) {
                student.getUser().setUserPassword(null); // 删除 userPassword 属性
                student.getUser().setUserRole(-1);        // 设置 userRole 为默认值（或者您认为合适的其他值）
            }
            r.setTotal(students.size());
        }
        r.ok();
        r.setData(students);
        return r;
    }

    @Override
    public MyResponseEntity<Object> getPersonalInfo(HttpServletRequest request) {
        // 获取请求头中的 Authorization
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7); // 7 是 "Bearer " 的长度
            // 解析 Token 获取用户 ID
            String userId = JWT.decode(token).getAudience().get(0);
            User user =  getUserById(Integer.parseInt(userId));
            int avatarId = user.getUserAvatarId();
            user.setUserPassword(null);
            UserWithSTF userWithSTF = new UserWithSTF();
            userWithSTF.setUser(user);
            userWithSTF.setAvatar(fileMapper.getFileById(avatarId));
            if(user.getUserRole() == 2){
                Teacher teacher = teacherMapper.getTeacherByUserId(user.getUserId());
                userWithSTF.setNumber(teacher.getTeacherNumber());
            }
            else{
                Student student = studentService.getStudentByUserId(user.getUserId());
                myFile faceFile =  fileMapper.getFileById(student.getStudentFaceId());
                userWithSTF.setNumber(student.getStudentNumber());
                userWithSTF.setFace(faceFile);
                userWithSTF.setMyClass(student.getMyClass());
            }
            return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS", userWithSTF);
        }


    @Override
    public User getUserById(int userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public String getAvatarByUserId(int parseInt) {
        return userMapper.getAvatarByUserId(parseInt);
    }

    @Override
    public int uploadAvatar(MultipartFile file) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String fileType = FileUtil.extName(originalFileName);
        long fileSize = file.getSize();
        //定义文件标识符
        String fileUid = SakeUtil.generateFileUid() + "." + fileType;

        //如果不存在相同MD5值的文件，进行文件上传
        File uploadDir =  new File(avatarFilePath);
        String fileUuid = avatarFilePath + fileUid;

        File uploadFile = new File(fileUuid);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        //先存储到磁盘
        file.transferTo(uploadFile);

        String fileMd5 = SakeUtil.getFileMD5Code(fileUuid);
        String fileUrl = null;

        //检查数据库中是否存在相同MD5值的文件
        myFile dbFile = fileMapper.getFileByMd5(fileMd5);
        if (dbFile != null) {
            fileUrl = dbFile.getFileUrl();
            uploadFile.delete();        //删除上传的文件
        }
        else{
            fileUrl =  "/files/avatars/" + fileUid;
        }
        //存储到数据库
        return saveFileToDB(originalFileName, fileType, fileSize, fileMd5, fileUrl, fileMapper);
    }

    @Override
    public void updateUserInfo(User user) {
        userMapper.updateUserInfo(user);
    }

    @Override
    public MyResponseEntity<Object> sendVerificationCode(String email) {
        try{
            Integer totalUser = userMapper.getTotalByEmail(email);
            if(totalUser == null || totalUser == 0){
                return new MyResponseEntity<>(CodeNums.ERROR,"用户不存在，请创建用户并完善邮箱");
            }
            String code = SakeUtil.generateVerificationCode();
            emailService.sendEmail(email,code);
            redisService.setValueWithExpire(SakeUtil.generateMD5(email),code,2*60);
            return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
        }catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CodeNums.SUCCESS, "发送验证码失败，请检查邮箱是否有误");
        }
    }

    @Override
    public MyResponseEntity<Object> setPassword(EmailCodeDTO emailCodeDTO) {
        userMapper.setPasswordByEmail(emailCodeDTO.getEmail(),emailCodeDTO.getPassword());
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
    }

    static int saveFileToDB(String originalFileName, String fileType, long fileSize, String fileMd5, String fileUrl, FileMapper fileMapper) {
        myFile sqlFile = new myFile();
        sqlFile.setFileName(originalFileName);
        sqlFile.setFileType(fileType);
        sqlFile.setFileSize(fileSize/1024);
        sqlFile.setFileUrl(fileUrl);
        sqlFile.setFileMd5(fileMd5);
        fileMapper.addFile(sqlFile);
        return sqlFile.getFileId();
    }
}
