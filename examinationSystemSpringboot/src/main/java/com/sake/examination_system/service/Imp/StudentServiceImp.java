package com.sake.examination_system.service.Imp;

import com.sake.examination_system.entity.Student;
import com.sake.examination_system.exception.ServiceException;
import com.sake.examination_system.mapper.ClassMapper;
import com.sake.examination_system.mapper.StudentMapper;
import com.sake.examination_system.mapper.StudentPaperMapper;
import com.sake.examination_system.mapper.UserMapper;
import com.sake.examination_system.service.StudentService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentServiceImp implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private StudentPaperMapper studentPaperMapper;

    @Resource
    UserMapper userMapper;

    @Override
    public MyResponseEntity<List<Student>> getAllStudentInfo() {
        MyResponseEntity<List<Student>> r = new MyResponseEntity<List<Student>>();
        List<Student> students = studentMapper.getAllStudentInfo();
        r.setTotal(students.size());
        for (Student student : students) {
            student.getUser().setUserPassword(null);     // 删除 userPassword 属性
            student.getUser().setUserRole(-1);     // 删除 userPassword 属性
        }
        r.setData(students);
        r.ok();
        return r;
    }

    @Override
    public Integer getAllStudentInfoByClassIdTotal(List<Integer> classIds) {
        return studentMapper.getAllStudentInfoByClassIdTotal(classIds);
    }

    @Transactional
    @Override
    public MyResponseEntity<Object> addStudent(Student student) {
        MyResponseEntity<Object> r = new MyResponseEntity<Object>();
        try {
            int resAddUser  = userMapper.addUser(student.getUser());
            int resAddStudent  = studentMapper.addStudent(student);
            if(resAddUser > 0 && resAddStudent > 0){
                r.ok();
            }
            else{
                r.error();
            }
        }catch (Exception e) {
            throw new ServiceException(CodeNums.ERROR,"用户信息为空");
        }
        return r;
    }

    @Override
    public MyResponseEntity<Integer> delStudentByUid(int userId) {
        MyResponseEntity<Integer> r = new MyResponseEntity<Integer>();
        int res = studentMapper.delStudentByUid(userId);
        if(res == 0){
            r.error();
        }
        else{
            r.ok();
        }
        return r;
    }

    @Override
    public List<Student> getPage(int pageNum, int pageSize,List<Integer>classIds) {
        return studentMapper.getPage(pageNum,pageSize,classIds);
    }

    @Override
    public List<Student> getStudentsByUserRealName(int pageNum, int pageSize, String userRealName,List<Integer> classIds) {
        return studentMapper.getStudentsByUserRealName(pageNum,pageSize,userRealName,classIds);
    }

    @Transactional
    @Override
    public MyResponseEntity<Object> removeStudentFromClass(int studentId) {
        studentPaperMapper.removePaperByStudentId(studentId);
        return studentMapper.removeStudentFromClass(studentId) > 0 ? new MyResponseEntity<Object>(CodeNums.SUCCESS,"移出成功"): new MyResponseEntity<Object>(CodeNums.ERROR,"移出失败");
    }

    @Override
    public MyResponseEntity<Object> batchRemoveStudentFromClass(List<Integer> studentIds) {
        for (Integer studentId : studentIds){
            int res = studentMapper.removeStudentFromClass(studentId);
            if(res == 0){
                return  new MyResponseEntity<Object>(CodeNums.ERROR,"批量移出失败");
            }
        }
        return new MyResponseEntity<Object>(CodeNums.SUCCESS,"移出成功");
    }

    @Override
    public Student getStudentByUserId(int userId) {
        return studentMapper.getStudentByUserId(userId);
    }

    @Override
    public List<Integer> getStudentIds(List<Integer> classIds) {
        return studentMapper.getIdByClassIds(classIds);
    }

    @Override
    public Integer getClassIdById(Integer studentId) {
        return studentMapper.getClassIdById(studentId);
    }


}
