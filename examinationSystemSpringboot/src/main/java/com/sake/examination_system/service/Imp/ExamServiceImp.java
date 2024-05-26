package com.sake.examination_system.service.Imp;

import com.sake.examination_system.entity.Class;
import com.sake.examination_system.entity.StudentAnswer;
import com.sake.examination_system.exception.ServiceException;
import com.sake.examination_system.mapper.*;
import com.sake.examination_system.service.ExamService;
import com.sake.examination_system.service.RedisService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Service
public class ExamServiceImp implements ExamService {
    @Resource
    ExamRecordsMapper examRecordsMapper;

    @Resource
    StudentPaperMapper studentPaperMapper;

    @Resource
    RedisService redisService;

    @Resource
    PaperClassMapper paperClassMapper;

    @Resource
    ClassMapper classMapper;

    @Resource
    StudentMapper studentMapper;

    @Transactional
    @Override
    public MyResponseEntity<Object> handleStudentEaxm(StudentAnswer examSubmission, HttpServletRequest request) {
        try {
            int studentId = examSubmission.getStudentId();
            int paperId = examSubmission.getPaperId();
            List<StudentAnswer.Answer> answers = examSubmission.getAnswers();

            for (StudentAnswer.Answer answer : answers) {
                int titleId = answer.getTitleId();
                Object rawAnswer = answer.getAnswer();
                String answerString = "";

                if (rawAnswer instanceof String) {
                    answerString = (String) rawAnswer;
                } else if (rawAnswer instanceof List) {
                    List<String> userAnswers = (List<String>) rawAnswer;
                    answerString = String.join("、", userAnswers);
                }
                if (studentPaperMapper.getPaperIsFinish(studentId,paperId)) {
                    return new MyResponseEntity<>(CodeNums.ERROR, "重复提交");
                }
                examRecordsMapper.addStudentAnswer(studentId, paperId, titleId, answerString);
            }
            //计算考试花费时间
            Integer userId =  SakeUtil.parseAuthorization(request);
            String key = SakeUtil.generateMD5(String.valueOf(userId));
            int totalTime =  (int) redisService.getValue(key);
            Long remainTime = redisService.getRemainingExpireTime(key);
            Long spendTime =  totalTime - remainTime;
            studentPaperMapper.updateSpendTime(paperId,studentId,spendTime);
            redisService.deleteValue(key);
            studentPaperMapper.isFinishPaper(studentId, paperId);
            return new MyResponseEntity<>(CodeNums.SUCCESS, "提交成功");
        }  catch (Exception e) {
            // 处理其他异常
            e.printStackTrace(); // 打印异常信息到控制台，你也可以使用日志框架记录到日志文件中
            throw new ServiceException(CodeNums.DUPLICATE_ENTRY, "重复提交");
        }
    }

    @Override
    public Boolean setExamTime(int userId, int examTotalTime) {
        String key = SakeUtil.generateMD5(String.valueOf(userId));
        if(redisService.hasKey(key)){
            return false;
        }
        redisService.setValueWithExpire(key,examTotalTime,examTotalTime);
        return true;
    }

    @Override
    public Long getEaxmRemainingTime(int userId) {
        return redisService.getRemainingExpireTime(SakeUtil.generateMD5(String.valueOf(userId)));
    }

    @Override
    public void removeEaxmRemainingTime(String userId) {
        redisService.deleteValue(SakeUtil.generateMD5(userId));
    }

    @Override
    public MyResponseEntity<Object> getGradeDetail(int paperId) {
        List<Class> classList = classMapper.getClassByIds(paperClassMapper.getClassIds(paperId));
        HashMap<String, Integer> data = new HashMap<>();
        for (Class myClass : classList) {
            List<Integer> studentIdList = studentMapper.getIdByClassId(myClass.getClassId());
            if (studentIdList.isEmpty()) {
                System.out.println("Class " + myClass.getClassName() + " has no students.");
                data.put(myClass.getClassName(), 0);
                continue; // Continue to the next class
            }

            Integer classScoreAvg = studentPaperMapper.getClassScoreAvg(studentIdList,paperId);
            if (classScoreAvg == null) {
                data.put(myClass.getClassName(), 0);
            } else {
                data.put(myClass.getClassName(), classScoreAvg);
            }
        }

        // If no classes have valid data, return an appropriate message
        if (data.isEmpty()) {
            return new MyResponseEntity<>(CodeNums.ERROR, "请批改学生试卷再查看", data);
        }

        return new MyResponseEntity<>(CodeNums.SUCCESS, "获取成功", data);
    }

}
