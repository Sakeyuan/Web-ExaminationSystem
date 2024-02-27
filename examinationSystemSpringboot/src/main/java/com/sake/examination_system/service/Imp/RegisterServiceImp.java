package com.sake.examination_system.service.Imp;
import com.sake.examination_system.entity.DTO.RegisterDTO;
import com.sake.examination_system.entity.Student;
import com.sake.examination_system.entity.Teacher;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.mapper.RegisterMapper;
import com.sake.examination_system.service.RegisterService;
import com.sake.examination_system.service.StudentService;
import com.sake.examination_system.service.TeacherService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class RegisterServiceImp implements RegisterService {
    @Resource
    RegisterMapper registerMapper;

    @Resource
    TeacherService teacherService;

    @Resource
    StudentService studentService;

    @Override
    public MyResponseEntity<Object> uploadRegisterInfo(String userPhone) {
        if(userPhone != null){
            if(registerMapper.checkUserPhone(userPhone) > 0){  //用户存在
                return new MyResponseEntity<>(CodeNums.EXIST, "用户已经存在");
            }
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
    }

    @Override
    public MyResponseEntity<Object> studentRegister(RegisterDTO registerDTO, int fileId) {
        if(registerDTO == null || registerDTO.getNumber().isEmpty() || registerDTO.getRole() == 0){
            return new MyResponseEntity<Object>(CodeNums.ERROR,"请完善信息");
        }

        String userName = SakeUtil.generateUsername();
        User user = new User();
        Student student = new Student();
        user.setUserName(userName);
        user.setUserRole(registerDTO.getRole());
        user.setUserPhone(registerDTO.getUserPhone());
        user.setUserPassword(registerDTO.getUserPassword());
        student.setUser(user);

        student.setStudentNumber(registerDTO.getNumber());
        student.setStudentFaceId(fileId);
        return studentService.addStudent(student);
    }

    @Override
    public MyResponseEntity<Object> teacherRegister(RegisterDTO registerDTO) {
        String userName = SakeUtil.generateUsername();
        User user = new User();
        Teacher teacher = new Teacher();
        user.setUserName(userName);
        user.setUserRole(registerDTO.getRole());
        user.setUserPhone(registerDTO.getUserPhone());
        user.setUserPassword(registerDTO.getUserPassword());
        teacher.setUser(user);

        teacher.setTeacherNumber(registerDTO.getNumber());
        return teacherService.addTeacher(teacher);
    }

}
