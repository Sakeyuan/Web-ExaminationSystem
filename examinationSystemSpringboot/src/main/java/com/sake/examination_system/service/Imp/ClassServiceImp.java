package com.sake.examination_system.service.Imp;

import com.sake.examination_system.entity.Class;
import com.sake.examination_system.entity.DTO.ClassDTO;
import com.sake.examination_system.entity.DTO.ClassWithT;
import com.sake.examination_system.entity.DTO.UserDTO;
import com.sake.examination_system.mapper.ClassMapper;
import com.sake.examination_system.mapper.StudentMapper;
import com.sake.examination_system.mapper.TeacherMapper;
import com.sake.examination_system.service.ClassService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassServiceImp implements ClassService {
    @Resource
    ClassMapper classMapper;

    @Resource
    TeacherMapper teacherMapper;

    @Resource
    StudentMapper studentMapper;

    @Override
    public MyResponseEntity<List<Class>> getAllClassByIdPage(int pageNum, int pageSize, String className, int id) {
        MyResponseEntity<List<Class>> r = new MyResponseEntity<List<Class>>();
        pageNum = (pageNum - 1) * pageSize;
        List<Class> myClass = null;
        if(id == 0){
            return null;
        }
        List<Integer> classIds= classMapper.getClassIdByTeacherId(id);
        if(classIds.isEmpty()){
            return  null;
        }
        if (className.isEmpty()) {
            myClass = classMapper.getPage(pageNum,pageSize,classIds);
        }
        else{  //按照名称查询学生信息
            className = "%" + className + "%";
            myClass = classMapper.getClassByName(pageNum,pageSize,className,classIds);
        }
        if(myClass.isEmpty()){
            return  null;
        }
        r.setTotal(classMapper.getAllClassTotalById(classIds));
        r.ok();
        r.setData(myClass);
        return r;
    }

    @Override
    public MyResponseEntity<Object> removeClass(int classId) {
        return classMapper.removeClass(classId) > 0 ? new MyResponseEntity<Object>(CodeNums.SUCCESS,"解散成功") : new MyResponseEntity<Object>(CodeNums.ERROR,"解散失败");
    }

    @Override
    public MyResponseEntity<Object> batchRemoveClass(List<Integer> classIds) {
        for (Integer classId : classIds){
            int res = classMapper.removeClass(classId);
            if(res == 0){
                return new MyResponseEntity<>(CodeNums.ERROR,"失败");
            }
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS,"解散成功");
    }

    @Override
    public int checkClassName(String className) {
        return classMapper.checkClassName(className);
    }

    @Override
    public MyResponseEntity<Object> createClassByName(String className,HttpServletRequest request) {
        if(checkClassName(className) > 0){
            return new MyResponseEntity<>(CodeNums.ERROR,"班级已经存在");
        }
        String classCode = SakeUtil.generateRandomClassCode();
        int teacherId = teacherMapper.getTeacherIdByUserId(SakeUtil.parseAuthorization(request));
        if (classMapper.createClassByName(className,classCode,teacherId) == 0){
            return new MyResponseEntity<>(CodeNums.ERROR,"创建失败");
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS,"班级创建成功");
    }

    @Override
    public MyResponseEntity<Object> getAllClassByTeacherId(int id) {
        List<Class> classList = classMapper.getAllClassByTeacherId(id);
        return new MyResponseEntity<Object>(CodeNums.SUCCESS,"",classList.size(),classList);
    }

    @Override
    public MyResponseEntity<Object> getClassByIds(String paperForClass) {
        if (paperForClass != null && !paperForClass.isEmpty()) {
            // 去除空格，按逗号分隔，然后转为整数数组
            int[] classIdsArray = Arrays.stream(paperForClass.replaceAll("\\s", "").split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            // 后续处理...
            // 将数组转为列表
            List<Integer> classIds = Arrays.stream(classIdsArray)
                    .boxed()
                    .collect(Collectors.toList());

            return new MyResponseEntity<>(CodeNums.SUCCESS, "", classMapper.getClassByIds(classIds));
        }
        return new MyResponseEntity<>(CodeNums.ERROR, "paperForClass is empty", null);

    }

    @Override
    public MyResponseEntity<Object> getAllClassByTid(int id) {
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS",classMapper.getAllClassByTeacherId(id));
    }

    @Override
    public MyResponseEntity<Object> getClassByToken(HttpServletRequest request) {
        Integer userId = SakeUtil.parseAuthorization(request);
        ClassWithT classWithT = classMapper.getClassByToken(userId);
        if(classWithT == null){
            return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
        }
        List<UserDTO>  userDTOS =  studentMapper.getAllStudentInClass(classWithT.getClassId());
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS",userDTOS.size(),classWithT,userDTOS);
    }

    @Transactional
    @Override
    public MyResponseEntity<Object> addClass(HttpServletRequest request, ClassDTO classDTO) {
        Integer classId = studentMapper.getClassIdByNameOrCode(classDTO.getClassName());
        if(classId == null){
            return new MyResponseEntity<>(CodeNums.ERROR,"加入失败，班级不存在");
        }
        studentMapper.addClass(SakeUtil.parseAuthorization(request),classId);
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
    }

    @Override
    public MyResponseEntity<Object> bowOutClass(HttpServletRequest request) {
        studentMapper.bowOutClass(SakeUtil.parseAuthorization(request));
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
    }
}
