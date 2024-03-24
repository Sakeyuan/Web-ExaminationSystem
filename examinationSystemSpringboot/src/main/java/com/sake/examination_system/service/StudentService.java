package com.sake.examination_system.service;

import com.sake.examination_system.entity.Student;
import com.sake.examination_system.util.MyResponseEntity;
import java.util.List;

public interface StudentService {
    MyResponseEntity<List<Student>> getAllStudentInfo();
    Integer getAllStudentInfoByClassIdTotal(List<Integer>classIds);
    MyResponseEntity<Object> addStudent(Student student);
    MyResponseEntity<Integer> delStudentByUid(int studentId);
    List<Student>getPage(int pageNum,int pageSize,List<Integer>classIds);
    List<Student> getStudentsByUserRealName(int pageNum,int pageSize,String userRealName,List<Integer>classIds);
    MyResponseEntity<Object> removeStudentFromClass(int studentId);
    MyResponseEntity<Object> batchRemoveStudentFromClass(List<Integer> studentIds);

    Student getStudentByUserId(int userId);

    List<Integer> getStudentIds(List<Integer> classIds);


    Integer getClassIdById(Integer studentId);
}
