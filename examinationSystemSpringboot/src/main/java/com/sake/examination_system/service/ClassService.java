package com.sake.examination_system.service;

import com.sake.examination_system.entity.Class;
import com.sake.examination_system.entity.DTO.ClassDTO;
import com.sake.examination_system.util.MyResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ClassService {

    MyResponseEntity<List<Class>> getAllClassByIdPage(int pageNum, int pageSize, String className, int id);

    MyResponseEntity<Object> removeClass(int classId);

    MyResponseEntity<Object> batchRemoveClass(List<Integer> classIds);

    int checkClassName(String className);

    MyResponseEntity<Object> createClassByName(String className,HttpServletRequest request);

    MyResponseEntity<Object> getAllClassByTeacherId(int id);

    MyResponseEntity<Object> getClassByIds(String paperForClass);

    MyResponseEntity<Object> getAllClassByTid(int id);


    MyResponseEntity<Object> getClassByToken(HttpServletRequest request);

    MyResponseEntity<Object> addClass(HttpServletRequest request, ClassDTO classDTO);

    MyResponseEntity<Object> bowOutClass(HttpServletRequest request);
}
