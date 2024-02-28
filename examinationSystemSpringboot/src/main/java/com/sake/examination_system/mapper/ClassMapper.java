package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.Class;
import com.sake.examination_system.entity.DTO.ClassWithT;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassMapper {
    List<Integer> getClassIdByTeacherId(int teacherId);
    List<Class>  getPage(int pageNum,int pageSize, List<Integer>classIds);

    Integer getAllClassTotalById(List<Integer> classIds);

    List<Class> getClassByName(int pageNum, int pageSize, String className, List<Integer> classIds);

    int removeClass(int classId);

    int checkClassName(String className);

    int createClassByName(String className,String classCode,int id);

    List<Class>getAllClassByTeacherId(int id);

    List<Class> getClassByIds(List<Integer> classIds);

    ClassWithT getClassByToken(Integer userId);

    Class getClassById(int classId);

}
