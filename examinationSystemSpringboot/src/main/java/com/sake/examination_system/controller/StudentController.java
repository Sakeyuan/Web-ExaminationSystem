package com.sake.examination_system.controller;
import com.sake.examination_system.entity.Student;
import com.sake.examination_system.service.Imp.StudentServiceImp;
import com.sake.examination_system.service.StudentService;
import com.sake.examination_system.service.UserService;
import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Resource(type = StudentServiceImp.class)
    StudentService studentService;

    @Resource
    UserService userService;

    @GetMapping("/getAllStudentInfo")
    public MyResponseEntity<List<Student>> getStudentAllInfo(){
        return studentService.getAllStudentInfo();
    }

    //分页查询
    //(pageNum - 1) * pageSize数据从哪个下标开始获取，获取多少条数据
    //select * from user limit (pageNum -1) * pageSize,pageSize
    @GetMapping("/page")
    public MyResponseEntity<List<Student>> getPage(@RequestParam int pageNum, @RequestParam int pageSize, @RequestParam int id, @RequestParam(required = false) String userRealName){
        return userService.getPage(pageNum,pageSize,id,userRealName);
    }

    @PostMapping("/addStudent")
    public MyResponseEntity<Object> addStudent(@RequestBody Student student){
        return studentService.addStudent(student);
     }

    @DeleteMapping("/removeStudentFromClass/{studentId}")
    public MyResponseEntity<Object> removeStudentFromClass(@PathVariable("studentId") int studentId){
        return studentService.removeStudentFromClass(studentId);
    }

    @PostMapping("/batchRemoveStudentFromClass")
    public MyResponseEntity<Object> batchRemoveStudentFromClass(@RequestBody List<Integer> studentIds){
        return studentService.batchRemoveStudentFromClass(studentIds);
    }

}
