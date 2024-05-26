package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.DTO.UserDTO;
import com.sake.examination_system.entity.Student;
import com.sake.examination_system.entity.User;
import org.apache.ibatis.annotations.Mapper;


import java.util.HashMap;
import java.util.List;

@Mapper
public interface StudentMapper {
    List<Student> getAllStudentInfo();
    Integer getAllStudentInfoByClassIdTotal(List<Integer>classIds);
    List<Student> getAllStudentInfoByClassId(List<Integer> classIds);
    int getAllStudentCountsByClassId(List<Integer> classIds);
    int addStudent(Student student);
    int delStudentByUid(int userId);
    List<Student>getPage(int pageNum,int pageSize, List<Integer> classIds);
    List<Student>getStudentsByUserRealName(int pageNum, int pageSize, String userRealName,List<Integer> classIds);
    int getStudentIdByPhone(String userPhone);
    Integer removeStudentFromClass(int classId);
    Integer inviteStudentToClass(int classId,String studentNumber);

    Integer getClassIdById(int id);

    List<Integer> getStudentIdByClassId(Integer classId);
    Student getStudentByUserId(int userId);

    void addClass(Integer userId, int classId);

    Integer getClassIdByNameOrCode(String className);

    List<UserDTO> getAllStudentInClass(Integer classId);

    void bowOutClass(Integer userId);

    List<HashMap<String, Integer>> getStudentCountsByClassId(List<Integer>classIds);


    String getFaceurlById(int id);

    Integer getClassIdByNumber(String studentNumber);

    List<Integer> getIdByClassId(int classId);

    List<Integer> getIdByClassIds(List<Integer> classIds);

    Integer getIdByStudentNumber(String studentNumber);
}
