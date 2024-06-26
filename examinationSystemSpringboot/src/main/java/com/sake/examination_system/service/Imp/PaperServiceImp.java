package com.sake.examination_system.service.Imp;

import com.google.gson.Gson;
import com.sake.examination_system.entity.*;
import com.sake.examination_system.entity.Class;
import com.sake.examination_system.entity.DTO.*;
import com.sake.examination_system.exception.ServiceException;
import com.sake.examination_system.mapper.*;
import com.sake.examination_system.service.PaperService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import io.swagger.models.auth.In;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
public class PaperServiceImp implements PaperService {
    @Resource
    PaperMapper paperMapper;

    @Resource
    TitleMapper titleMapper;

    @Resource
    StudentMapper studentMapper;

    @Resource
    StudentPaperMapper studentPaperMapper;

    @Resource
    ExamRecordsMapper examRecordsMapper;

    @Resource
    PaperClassMapper paperClassMapper;

    @Resource
    ClassMapper classMapper;

    @Resource
    RedisServiceImp redisServiceImp;

    @Resource
    private TaskScheduler taskScheduler;

    private ScheduledFuture<?> taskFuture;

    private static Set<Integer> preReleasedSet = new ConcurrentSkipListSet<>();

    @Async
    @Scheduled(fixedRate = 60000) // 60秒 = 1分钟
    public void startTask() {
        if (preReleasedSet.isEmpty() && taskFuture != null) {
            System.out.println("停止定时任务");
            taskFuture.cancel(true); // 停止定时任务
            return;
        }
        for (Integer paperId : preReleasedSet){
            long PUBLISH_DATE_TIMESTAMP = (long)redisServiceImp.getValue("paperId:" + paperId);
            long currentTime = System.currentTimeMillis();
            if (currentTime >= PUBLISH_DATE_TIMESTAMP) {
                // 如果当前时间大于等于发布日期，就执行发布试卷的操作
                publishPaper(paperId);
                System.out.println(paperId + "试卷已发布！");
            }
        }
    }

    public void startScheduler() {
        Runnable task = this::startTask;
        taskFuture = taskScheduler.schedule(task, new CronTrigger("0 * * * * *")); // 每分钟执行一次
    }

    public void publishPaper(int paperId) {
       paperMapper.setIsReleased(paperId);
       releasePaperToStudent(paperId);
       redisServiceImp.deleteValue("paperId:" + paperId);
       preReleasedSet.remove(paperId);
    }

    /**
     * 计算三年内试卷的题目重复率
     * @param paperDTO
     * @param IntersectionRate
     * @return
     */
    private Boolean matchPaperRepeatRate(PaperDTO paperDTO,double IntersectionRate){
        List<Integer> totalIdList1 =  new ArrayList<>();
        for (PaperDTO.SubheadingData  subheadingData : paperDTO.getSubheadings()){
            List<Integer> idList =  subheadingData.getIds();
            totalIdList1.addAll(idList);
        }
        List<Paper> allPapers = paperMapper.getAllPaperByTeacherIdLimitDate(paperDTO.getTeacherId());
        for (Paper paper : allPapers) {
            List<PaperDTO.SubheadingData> subheadingDataList = (List<PaperDTO.SubheadingData>) paper.getPaperContent();
            List<Integer> totalIdList2 =  new ArrayList<>();
            for (PaperDTO.SubheadingData  subheadingData : subheadingDataList){
                List<Integer> idList =  subheadingData.getIds();
                totalIdList2.addAll(idList);
            }
            // 计算交集
            List<Integer> intersection = new ArrayList<>(totalIdList1);
            intersection.retainAll(totalIdList2);

            // 计算交集率
            double intersectionRate = (double) intersection.size() / Math.min(totalIdList1.size(), totalIdList2.size()) * 100;

            if(intersectionRate > IntersectionRate){
                return true;
            }
        }
        return false;
    }


    @Transactional
    @Override
    public MyResponseEntity<Object> releasePaper(PaperDTO paperDTO) {
        Paper paper = new Paper();
        Integer totalTime =  paperDTO.getExamTotalTime();
        paper.setPaperTotalTime(totalTime.longValue());
        paper.setPaperName(paperDTO.getPaperName());
        paper.setTeacherId(paperDTO.getTeacherId());
        paper.setReleased(paperDTO.getIsReleased() == 1);
        paper.setAllowCheck(paperDTO.getIsAllowCheck() == 1);
        try{
            Gson gson = new Gson();
            int scores = 0;
            List<PaperDTO.SubheadingData> subheadingData = paperDTO.getSubheadings();
            for (PaperDTO.SubheadingData subheading: subheadingData){
                List<Integer> subheadingIds = subheading.getIds();
                if(subheadingIds.isEmpty()){
                    throw new ServiceException(CodeNums.ERROR,"题目列表为空");
                }
                List<Map<String, Object>> titleMaps = titleMapper.getTitleById(subheadingIds);
                for (Map<String, Object> titleMap : titleMaps){
                    String titleContentJson = (String) titleMap.get("title_content");
                    TitleDTO titleDTO = gson.fromJson(titleContentJson, TitleDTO.class);
                    scores +=  titleDTO.getScores();
                }
            }
            paper.setPaperScore(scores);
            String jsonString  = gson.toJson(subheadingData);
            paper.setPaperContent(jsonString);
            paperMapper.releasePaper(paper);
            int paperId = paper.getPaperId();
            //预发布

            for(Integer classId : paperDTO.getSelectedClasses()){
                paperClassMapper.addOne(paperId,classId);
            }

            if(paperDTO.getPreReleaseDate() != 0){
                preReleasedSet.add(paperId);
                redisServiceImp.setValue("paperId:"+paperId,paperDTO.getPreReleaseDate());
                // 每分钟检查一次是否到达试卷发布日期
                startScheduler();
                return  new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
            }

            StudentPaper studentPaper = new StudentPaper();
            List<Integer> classIdList = paperDTO.getSelectedClasses();

            studentPaper.setPaperId(paperId);
            for (Integer classId : classIdList){
                List<Integer> studentIdList = studentMapper.getStudentIdByClassId(classId);
                for(Integer studentId : studentIdList){
                    studentPaper.setStudentId(studentId);
                    studentPaperMapper.addPaper(studentPaper);
                }
            }
        }catch (Exception e){
            throw new ServiceException(CodeNums.ERROR,e.getMessage());
        }
        return  new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
    }


    private void releasePaperToStudent(int paperId){
        StudentPaper studentPaper = new StudentPaper();
        List<Integer> classIdList = paperClassMapper.getClassIds(paperId);
        studentPaper.setPaperId(paperId);
        for (Integer classId : classIdList){
            List<Integer> studentIdList = studentMapper.getStudentIdByClassId(classId);
            for(Integer studentId : studentIdList){
                studentPaper.setStudentId(studentId);
                studentPaperMapper.addPaper(studentPaper);
            }
        }
    }

    @Override
    public MyResponseEntity<Object> getAllPaperListByTeacherId(PageDTO pageDTO) {
       List<Paper> paperList =  paperMapper.getAllPaperListByTeacherId(pageDTO);
       List<PaperWithClassDTO>paperWithClassDTOS = new ArrayList<>();
       for (Paper paper : paperList){
           List<Integer> classIds =  paperClassMapper.getClassIds(paper.getPaperId());
           List<Class> classList = classMapper.getClassByIds(classIds);
           PaperWithClassDTO paperWithClassDTO = new PaperWithClassDTO();
           paperWithClassDTO.setPaper(paper);
           paperWithClassDTO.setClassList(classList);
           paperWithClassDTOS.add(paperWithClassDTO);
       }
       return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS",paperMapper.getAllPaperListByTeacherIdTotal(pageDTO),paperWithClassDTOS);
    }

    @Override
    public MyResponseEntity<Object> deletePaperById(int id) {
        return paperMapper.deletePaperById(id) > 0 ? new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS") : new MyResponseEntity<>(CodeNums.ERROR, "删除失败");
    }

    @Override
    public MyResponseEntity<Object> batchDelById(List<Integer> ids) {
        for (Integer id : ids){
            int res = paperMapper.deletePaperById(id);
            if(res == 0){
                return  new MyResponseEntity<>(CodeNums.ERROR, "删除失败");
            }
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
    }


    @Override
    public MyResponseEntity<Object> getPaperByStudentId(PageDTO pageDTO, boolean isFinish, Boolean isCorrect) {
        Integer classId = studentMapper.getClassIdById(pageDTO.getId());
        if(classId == null){
            return new MyResponseEntity<>(CodeNums.SUCCESS, "未加入班级");
        }
        try{
            List<Integer> paperIdList = studentPaperMapper.getPaperIdByStudent(pageDTO.getId(), isFinish,isCorrect);
            List<StudentPaper>studentPaperList = new ArrayList<>();
            int studentId = pageDTO.getId();
            for (int paperId : paperIdList){
                studentPaperList.add(studentPaperMapper.getExamRecordsByPidAndSid(paperId,studentId));
            }
            List<Paper> paperList = paperMapper.getAllPaperByPage(paperIdList, pageDTO);
            return new MyResponseEntity<>(CodeNums.SUCCESS, "获取成功", studentPaperMapper.getPaperIdByStudentTotal(pageDTO.getId(), isFinish,isCorrect), paperList,studentPaperList);
        }catch (Exception e){
            throw new ServiceException(CodeNums.ERROR,"试卷为空");
        }
    }

    @Override
    public MyResponseEntity<Object> getCorrectPaper(PageDTO pageDTO, boolean isFinish, Boolean isCorrect) {
        List<PaperWithStudentDTO> paperList = studentPaperMapper.getCorrectPaper(pageDTO,isFinish,isCorrect);
        Integer total = studentPaperMapper.getCorrectPaperTotal(pageDTO,isFinish,isCorrect);
        return new MyResponseEntity<>(CodeNums.SUCCESS, "获取成功",total, paperList);
    }

    @Override
    public MyResponseEntity<Object> getOnePaperWithContentAndAnswer(int paperId, int studentId) {
        List<Integer> paperIdList = new ArrayList<>();
        paperIdList.add(paperId);
        List<Paper>paperList = paperMapper.getPaperById(paperIdList);

        //修改选择题、判断题
        List<Title>titles = new ArrayList<>();
        Gson gson = new Gson();
        for (Paper paper : paperList) {
            List<PaperDTO.SubheadingData> subheadingDataList = (List<PaperDTO.SubheadingData>) paper.getPaperContent();
            for (PaperDTO.SubheadingData subheadingData : subheadingDataList) {
                List<Map<String, Object>> titleMaps = titleMapper.getTitleById(subheadingData.getIds());
                for (Map<String, Object> titleMap : titleMaps) {
                    Title title = new Title();
                    title.setTitleId((int) titleMap.get("title_id"));
                    title.setTitleType(TitleTypeEnum.getDescriptionByCode((int)titleMap.get("title_type")));

                    // 格式化时间戳为年-月-日
                    Timestamp timestamp = (Timestamp) titleMap.get("title_create_stamp");

                    title.setTitleCreateStamp(SakeUtil.getFormatTime(timestamp));       // 设置为格式化后的日期字符串

                    title.setTeacherId((int) titleMap.get("teacher_id"));

                    String titleContentJson = (String) titleMap.get("title_content");
                    TitleDTO titleDTO = gson.fromJson(titleContentJson, TitleDTO.class);

                    title.setTitleContent(titleDTO);
                    titles.add(title);
                }
            }
        }

        //学生答案
        int scores = 0;
        Map<Integer, ExamRecords> studentRecordsMap = new HashMap<>();
        List<ExamRecords> studentPaperList = examRecordsMapper.getRecordsByStudentIdAndPaperId(paperId, studentId);
        for (ExamRecords studentRecord : studentPaperList) {
            studentRecordsMap.put(studentRecord.getTitleId(), studentRecord);
        }
        for (Title title : titles){
            TitleTypeEnum titleTypeEnum =  title.getTitleContent().getType();
            if(titleTypeEnum == TitleTypeEnum.SINGLE_TITLE || titleTypeEnum == TitleTypeEnum.MULTIPLE_TITLE || titleTypeEnum == TitleTypeEnum.JUDGE_TITLE){
                int titleId = title.getTitleId();
                //标准答案
                String standardAnswer =  title.getTitleContent().getAnswer();
                ExamRecords studentRecord = studentRecordsMap.get(titleId);
                // 如果存在学生的答题记录，则进行比较和更新
                if (studentRecord != null) {
                    String studentAnswer = studentRecord.getAnswer();
                    boolean isCorrect = false;
                    // 对比学生答案和标准答案
                    if(titleTypeEnum == TitleTypeEnum.SINGLE_TITLE){
                        isCorrect = singleCompareAnswers(studentAnswer,standardAnswer);
                    }
                    else if(titleTypeEnum == TitleTypeEnum.MULTIPLE_TITLE){
                        isCorrect = multipleCompareAnswers(studentAnswer,standardAnswer);
                    }
                    else if(titleTypeEnum == TitleTypeEnum.JUDGE_TITLE){
                        isCorrect = judgeCompareAnswers(studentAnswer,standardAnswer);
                    }
                    // 更新答题记录中的是否正确字段
                    studentRecord.setCorrect(true);
                    if(isCorrect){
                        int score = title.getTitleContent().getScores();
                        scores += score;
                        studentRecord.setScores(score);
                        try{
                            examRecordsMapper.updateRecord(studentRecord,paperId,studentId,titleId);
                        }catch (Exception e){
                            throw new ServiceException(CodeNums.ERROR,e.getMessage());
                        }
                    }
                }
            }
        }

        List<Paper> resultPaperList = new ArrayList<>();
        titleIdsToContent(paperList,resultPaperList);
        List<ExamRecords> examRecordsList =  examRecordsMapper.getRecordsByStudentIdAndPaperId(paperId,studentId);

        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS", scores, resultPaperList.get(0),examRecordsList);
    }

    @Override
    public MyResponseEntity<Object> getOnePaperWithContentAndScore(int paperId, int studentId) {
        List<Integer> paperIdList = new ArrayList<>();
        paperIdList.add(paperId);
        List<Paper>paperList = paperMapper.getPaperById(paperIdList);
        List<Paper> resultPaperList = new ArrayList<>();
        titleIdsToContent(paperList,resultPaperList);
        List<ExamRecords> examRecordsList =  examRecordsMapper.getRecordsByStudentIdAndPaperId(paperId,studentId);
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS", 1,resultPaperList.get(0),examRecordsList);
    }

    @Override
    public MyResponseEntity<Object> getStudentHomeData(int id) {
        int hadFinishNumbers = studentPaperMapper.getCountsStudentPaper(id,true,false);
        int unFinishNumbers = studentPaperMapper.getCountsStudentPaper(id,false,false);
        int hadCorrectNumbers = studentPaperMapper.getCountsStudentPaper(id,true,true);
        int paperTotalNumbers = hadFinishNumbers + unFinishNumbers + hadCorrectNumbers;
        HashMap<String,Integer>data = new HashMap<>();
        data.put("paperTotalNumbers",paperTotalNumbers);
        data.put("unFinishNumbers",unFinishNumbers);
        data.put("hadCorrectNumbers",hadCorrectNumbers);

        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS", data);
    }

    @Override
    public MyResponseEntity<Object> getOnePaperWithContentAndScoreAndEmpty(int paperId) {
        List<Integer> paperIdList = new ArrayList<>();
        paperIdList.add(paperId);
        List<Paper>paperList = paperMapper.getPaperById(paperIdList);
        List<Paper> resultPaperList = new ArrayList<>();
        titleIdsToContent(paperList,resultPaperList);
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS", 1,resultPaperList.get(0));
    }

    @Override
    public MyResponseEntity<Object> addPaperClass(PaperAddClassDTO paperAddClassDTO) {
        try {
            List<Integer> classIds = classMapper.getIdSByClassName(paperAddClassDTO.getClassList());
            StudentPaper studentPaper = new StudentPaper();
            int paperId = paperAddClassDTO.getPaperId();
            studentPaper.setPaperId(paperId);
            for (Integer classId : classIds){
                paperClassMapper.addOne(paperId,classId);
                List<Integer>studentIdList =  studentMapper.getIdByClassId(classId);
                for (Integer studentId : studentIdList){
                    studentPaper.setStudentId(studentId);
                    studentPaperMapper.addPaper(studentPaper);
                }
            }
            return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
        } catch (DuplicateKeyException e) {
            // 处理数据库唯一约束异常
            return new MyResponseEntity<>(CodeNums.SUCCESS,"SUCCESS");
        } catch (Exception e) {
            // 处理其他异常
            return new MyResponseEntity<>(CodeNums.ERROR, "Internal server error: " + e.getMessage());
        }
    }


    @Override
    public MyResponseEntity<Object> favorite(ExamRecords examRecords) {
        examRecordsMapper.updateFavorite(examRecords);
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
    }

    @Override
    public MyResponseEntity<Object> getFavorite(int studentId) {
        List<ExamRecords>examRecordsList = examRecordsMapper.getExamRecodsByStudentIdAndFavorite(studentId);
        if(examRecordsList.isEmpty()){
            return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
        }
        List<Integer> titleIds = examRecordsList.stream()
                .map(ExamRecords::getTitleId)
                .collect(Collectors.toList());
        List<Map<String, Object>> titleMaps = titleMapper.getTitleById(titleIds);
        Gson gson = new Gson();
        List<Title>titles = new ArrayList<>();
        for (Map<String, Object> titleMap : titleMaps) {
            Title title = new Title();
            title.setTitleId((int) titleMap.get("title_id"));
            title.setTitleType(TitleTypeEnum.getDescriptionByCode((int)titleMap.get("title_type")));

            // 格式化时间戳为年-月-日
            Timestamp timestamp = (Timestamp) titleMap.get("title_create_stamp");

            title.setTitleCreateStamp(SakeUtil.getFormatTime(timestamp));       // 设置为格式化后的日期字符串

            title.setTeacherId((int) titleMap.get("teacher_id"));

            String titleContentJson = (String) titleMap.get("title_content");
            TitleDTO titleDTO = gson.fromJson(titleContentJson, TitleDTO.class);

            title.setTitleContent(titleDTO);
            titles.add(title);
        }
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS", 1,titles,examRecordsList);
    }

    @Override
    public MyResponseEntity<Object> cancelFavorite(int examId) {
        examRecordsMapper.cancelFavorite(examId);
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
    }

    @Override
    public MyResponseEntity<Object> setPaperReleased(Integer paperId) {
        publishPaper(paperId);
        return new MyResponseEntity<>(CodeNums.SUCCESS, "SUCCESS");
    }

    @Override
    public MyResponseEntity<Object> checkRepetition(PaperDTO paperDTO) {
        if(matchPaperRepeatRate(paperDTO,0.8)){
            return new MyResponseEntity<>(CodeNums.SUCCESS, "重复率大于 80% ");
        }
        else {
            return new MyResponseEntity<>(CodeNums.ERROR, "重复率小于 80%");
        }
    }

    @Override
    public boolean singleCompareAnswers(String studentAnswer, String standardAnswer) {
        return studentAnswer.equals(standardAnswer);
    }

    @Override
    public boolean multipleCompareAnswers(String studentAnswer, String standardAnswer) {
        Set<String> studentOptions = new HashSet<>(Arrays.asList(studentAnswer.split("、")));
        Set<String> standardOptions = new HashSet<>(Arrays.asList(standardAnswer.split("、")));
        // 检查两个集合是否包含相同的元素
        return studentOptions.equals(standardOptions);
    }

    @Override
    public boolean judgeCompareAnswers(String studentAnswer, String standardAnswer) {
        return studentAnswer.equals(standardAnswer);
    }

    @Override
    public MyResponseEntity<Object> submitCorrectAnswer(CorrectAnswerDTO correctAnswerDTO) {
        int paperId = correctAnswerDTO.getPaperId();
        int studentId =  correctAnswerDTO.getStudentId();
        try{
            for (Map.Entry<String, Integer> correctScore : correctAnswerDTO.getCorrectResult().entrySet()) {
                String titleId = correctScore.getKey();
                Integer scores = correctScore.getValue();
                examRecordsMapper.submitCorrectAnswer(paperId,studentId,titleId,scores);
            }
            studentPaperMapper.submitCorrect(paperId,studentId,correctAnswerDTO.getTotalScore());
            return new MyResponseEntity<Object>(CodeNums.SUCCESS,"批改成功");
        }catch (Exception e) {
            return new MyResponseEntity<Object>(CodeNums.ERROR,e.getMessage());
        }
    }


    private void titleIdsToContent(List<Paper> paperList, List<Paper> resultPaperList) {
        for (Paper paper : paperList) {
            List<PaperDTO.SubheadingData> subheadingDataList = (List<PaperDTO.SubheadingData>) paper.getPaperContent();
            paper.clearPaperContent();

            List<String> subheadingContentList = new ArrayList<>();

            for (PaperDTO.SubheadingData subheadingData : subheadingDataList) {
                List<Map<String, Object>> titleMaps = titleMapper.getTitleById(subheadingData.getIds());
                List<Map<String, Object>> titleContents = new ArrayList<>();

                for (Map<String, Object> titleMap : titleMaps) {
                    Map<String, Object> titleContent = new HashMap<>();
                    titleContent.put("content", titleMap.get("title_content"));
                    titleContent.put("titleId", titleMap.get("title_id"));
                    titleContent.put("titleType", titleMap.get("title_type"));
                    titleContents.add(titleContent);
                }

                Map<String, Object> subheadingContent = new HashMap<>();
                subheadingContent.put("title", subheadingData.getTitle());
                subheadingContent.put("contents", titleContents);

                String paperContent = new Gson().toJson(subheadingContent);
                subheadingContentList.add(paperContent);
            }
            String paperContent = String.format("[%s]", String.join(",", subheadingContentList));

            paper.addPaperContent(paperContent);
            paper.removeContentLastStr();
            resultPaperList.add(paper);
        }
    }
}
