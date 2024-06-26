package com.sake.examination_system.service.Imp;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.sake.examination_system.entity.DTO.InviteDTO;
import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.SingleInviteDTO;
import com.sake.examination_system.entity.Student;
import com.sake.examination_system.entity.StudentPaper;
import com.sake.examination_system.entity.Teacher;
import com.sake.examination_system.exception.ServiceException;
import com.sake.examination_system.mapper.*;
import com.sake.examination_system.service.TeacherService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
public class TeacherServiceImp implements TeacherService {
    @Resource
    StudentMapper studentMapper;

    @Resource
    RedisServiceImp redisServiceImp;

    @Resource
    TeacherMapper teacherMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    ClassMapper classMapper;

    @Resource
    TitleMapper titleMapper;

    @Resource
    PaperMapper paperMapper;

    @Resource
    PaperClassMapper paperClassMapper;

    @Resource
    StudentPaperMapper studentPaperMapper;

    @Override
    public MyResponseEntity<Object> importStudent(MultipartFile file) {
        try{
            InputStream inputStream = file.getInputStream();
            ExcelReader excelReader = ExcelUtil.getReader(inputStream);
            List<Object> numberData = excelReader.readColumn(1, 1);
            List<Object> nameData = excelReader.readColumn(0, 1);
            List<Map<String,String >> studentNumbers = new ArrayList<>();

            for (int i = 0; i < numberData.size(); ++i) {
                Object number = numberData.get(i);
                Object name = nameData.get(i);
                if (number != null && name != null) {
                    String numberString = number.toString();
                    String nameString = name.toString();

                    Map<String, String> studentData = new HashMap<>();
                    studentData.put("number", numberString);
                    studentData.put("name", nameString);
                    studentNumbers.add(studentData);
                }
            }
            return new MyResponseEntity<>(CodeNums.SUCCESS,"解析成功",studentNumbers.size(),studentNumbers);
        }catch (Exception e){
            throw new ServiceException(CodeNums.ERROR,"表格解析错误，请检查模版是否正确");
        }
    }

    @Transactional
    @Override
    public MyResponseEntity<Object> addTeacher(Teacher teacher) {
        MyResponseEntity<Object> r = new MyResponseEntity<Object>();
        try {
            int resAddUser  = userMapper.addUser(teacher.getUser());
            int resAddTeacher  = teacherMapper.addTeacher(teacher);
            if(resAddUser > 0 && resAddTeacher > 0){
                r.ok();
            }
            else{
                r.error();
            }
        }catch (Exception e) {
            throw new ServiceException(CodeNums.ERROR,e.getMessage());
        }
        return r;
    }

    @Override
    public MyResponseEntity<Object> inviteStudent(InviteDTO studentList) {
        int classId = Integer.parseInt(studentList.getClassId());
        HashMap<String, String> data = new HashMap<>();
        for (String studentInfo : studentList.getData()) {
            String[] parts = studentInfo.split("-");
            if (parts.length == 2) {
                String name = parts[0];
                String number = parts[1];
                Integer hadClass =  studentMapper.getClassIdByNumber(number);
                Integer studentId = studentMapper.getIdByStudentNumber(number);
                if(hadClass == null && studentId != null){
                    int res = studentMapper.inviteStudentToClass(classId,number);
                    List<Integer> paperIds = paperClassMapper.getPaperIdsByClassId(classId);
                    for (Integer paperId : paperIds){
                        studentPaperMapper.addPaper(new StudentPaper(paperId,studentId));
                    }
                    if(res == 0){
                        data.put(name,number);
                    }
                }
            }
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS,"邀请成功",data);
    }

    @Override
    public MyResponseEntity<Object> singleInvite(SingleInviteDTO studentInfo) {
        int classId = Integer.parseInt(studentInfo.getClassId());
        Integer hadClass =  studentMapper.getClassIdByNumber(studentInfo.getStudentNumber());
        if(hadClass != null){
            return new MyResponseEntity<>(CodeNums.ERROR,"邀请失败,学生已经存在班级");
        }
        if(studentMapper.inviteStudentToClass(classId,studentInfo.getStudentNumber()) == 0){
            return new MyResponseEntity<>(CodeNums.ERROR,"邀请失败，学生不存在");
        }
        int studentId = studentMapper.getIdByStudentNumber(studentInfo.getStudentNumber());
        List<Integer> paperIds = paperClassMapper.getPaperIdsByClassId(classId);
        for (Integer paperId : paperIds){
            studentPaperMapper.addPaper(new StudentPaper(paperId,studentId));
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS,"邀请成功");
    }

    @Override
    public MyResponseEntity<Object> getHomeData(HttpServletRequest request) {
        HashMap<String,Integer> countData = new HashMap<String,Integer>();
        int userId = SakeUtil.parseAuthorization(request);
        int teacherId = teacherMapper.getTeacherIdByUserId(userId);
        List<Integer>classIds = classMapper.getClassIdByTeacherId(teacherId);
        int tileNumbers = titleMapper.getTitlesCountByTeacherId(new PageDTO(teacherId));
        List<List<HashMap<String, Integer>>> pipeData = new ArrayList<>();
        if(classIds.isEmpty()){
            countData.put("titleNumbers",tileNumbers);
            countData.put("studentNumbers",0);
            countData.put("classNumbers",0);
            countData.put("paperNumbers",0);
        }
        else{
            int studentNumbers = studentMapper.getAllStudentCountsByClassId(classIds);
            int classNumbers = classIds.size();
            int paperNumbers = paperMapper.getPaperCountByTeacherId(teacherId);
            countData.put("studentNumbers",studentNumbers);
            countData.put("classNumbers",classNumbers);
            countData.put("paperNumbers",paperNumbers);
            countData.put("titleNumbers",tileNumbers);
            List<HashMap<String, Integer>> pipe1Data = studentMapper.getStudentCountsByClassId(classIds);
            List<HashMap<String, Integer>> pipe2Data = paperClassMapper.getListByTeacherId(teacherId);
            pipeData.add(pipe1Data);
            pipeData.add(pipe2Data);
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS",countData.size(),countData,pipeData);
    }

    @Override
    public void exportClassStudent(HttpServletResponse httpServletResponse, int classId) throws IOException {
        List<Integer>classIds  = new ArrayList<>();
        classIds.add(classId);
        List<Student> students = studentMapper.getAllStudentInfoByClassId(classIds);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Student student : students) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            dataMap.put("ID", student.getStudentId());
            dataMap.put("学号", student.getStudentNumber());
            dataMap.put("用户名", student.getUser().getUserName());
            dataMap.put("真名", student.getUser().getUserRealName());
            dataMap.put("性别", student.getUser().getUserGender());
            dataMap.put("所属班级", student.getMyClass().getClassName());
            dataMap.put("邮箱", student.getUser().getUserEmail());
            dataMap.put("手机", student.getUser().getUserPhone());
            dataList.add(dataMap);
        }

        // 创建 ExcelWriter
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);

        // 设置表头别名
        excelWriter.addHeaderAlias("ID", "ID").setColumnWidth(1,10);
        excelWriter.addHeaderAlias("学号", "学号").setColumnWidth(2,20);
        excelWriter.addHeaderAlias("用户名", "用户名").setColumnWidth(3,15);
        excelWriter.addHeaderAlias("真名", "真名").setColumnWidth(4,15);
        excelWriter.addHeaderAlias("性别", "性别").setColumnWidth(5,10);
        excelWriter.addHeaderAlias("所属班级", "所属班级").setColumnWidth(5,10);
        excelWriter.addHeaderAlias("邮箱", "邮箱").setColumnWidth(6,35);
        excelWriter.addHeaderAlias("手机", "手机").setColumnWidth(7,35);

        // 将 dataList 写入 ExcelWriter
        excelWriter.write(dataList, true);

        // 设置响应头，发送到浏览器
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("学生信息", "UTF-8");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream out = httpServletResponse.getOutputStream();
        excelWriter.flush(out, true);
        out.close();
        excelWriter.close();
    }

    @Override
    public MyResponseEntity<Object> getOnlineStudents(String id) {
        return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS",redisServiceImp.getValue(id + SakeUtil.TEACHER_KEY));
    }

    @Override
    public void exportInfo(HttpServletResponse httpServletResponse,int id) throws IOException {
        List<Integer>classIds =  classMapper.getClassIdByTeacherId(id);
        if(classIds == null){
            return;
        }
        List<Student> students = studentMapper.getAllStudentInfoByClassId(classIds);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Student student : students) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            dataMap.put("ID", student.getStudentId());
            dataMap.put("学号", student.getStudentNumber());
            dataMap.put("用户名", student.getUser().getUserName());
            dataMap.put("真名", student.getUser().getUserRealName());
            dataMap.put("性别", student.getUser().getUserGender());
            dataMap.put("所属班级", student.getMyClass().getClassName());
            dataMap.put("邮箱", student.getUser().getUserEmail());
            dataMap.put("手机", student.getUser().getUserPhone());
            dataList.add(dataMap);
        }

        // 创建 ExcelWriter
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 设置表头别名
        excelWriter.addHeaderAlias("ID", "ID").setColumnWidth(1,10);
        excelWriter.addHeaderAlias("学号", "学号").setColumnWidth(2,20);
        excelWriter.addHeaderAlias("用户名", "用户名").setColumnWidth(3,15);
        excelWriter.addHeaderAlias("真名", "真名").setColumnWidth(4,15);
        excelWriter.addHeaderAlias("性别", "性别").setColumnWidth(5,10);
        excelWriter.addHeaderAlias("所属班级", "所属班级").setColumnWidth(5,10);
        excelWriter.addHeaderAlias("邮箱", "邮箱").setColumnWidth(6,35);
        excelWriter.addHeaderAlias("手机", "手机").setColumnWidth(7,35);

        // 将 dataList 写入 ExcelWriter
        excelWriter.write(dataList, true);

        // 设置响应头，发送到浏览器
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("学生信息", "UTF-8");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream out = httpServletResponse.getOutputStream();
        excelWriter.flush(out, true);
        out.close();
        excelWriter.close();
    }
}
