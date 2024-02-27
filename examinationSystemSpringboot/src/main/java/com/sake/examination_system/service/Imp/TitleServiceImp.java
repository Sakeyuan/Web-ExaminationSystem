package com.sake.examination_system.service.Imp;

import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.TitleDTO;
import com.sake.examination_system.entity.TitleTypeEnum;
import com.sake.examination_system.entity.Title;
import com.sake.examination_system.mapper.TitleMapper;
import com.sake.examination_system.service.TitleService;
import com.sake.examination_system.util.CodeNums;
import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class TitleServiceImp implements TitleService {

    @Resource
    TitleMapper titleMapper;

    @Override
    public MyResponseEntity<Object> createTitle(TitleDTO titleDTO) {
        Gson gson = new Gson();
        // 将对象转换为 JSON 字符串
        String singleTitleDTOJson = gson.toJson(titleDTO);
        titleMapper.createTitle(titleDTO.getType().getCode(),singleTitleDTOJson, titleDTO.getTeacherId());

        return new MyResponseEntity<Object>(CodeNums.SUCCESS,"");
    }

    @Override
    public MyResponseEntity<Object> getAllTitlesByTeacherId(PageDTO pageDTO) {
        List<Title> titles = new ArrayList<>();
        List<Map<String, Object>> titleMaps = titleMapper.getAllTitlesByTeacherId(pageDTO);
        Gson gson = new Gson();
        // Convert List<Map<String, Object>> to List<Title>
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
        return new MyResponseEntity<Object>(CodeNums.SUCCESS, "",titleMapper.getTitlesCountByTeacherId(pageDTO) , titles);
    }

    @Override
    public MyResponseEntity<Object> deleteTitleById(int id) {
        return titleMapper.deleteTitleById(id) > 0 ?  new MyResponseEntity<Object>(CodeNums.SUCCESS, "") : new MyResponseEntity<Object>(CodeNums.ERROR, "");
    }

    @Override
    public MyResponseEntity<Object> batchDeleteTitleById(List<Integer> titleIds) {
        for (Integer titleId : titleIds){
            int res = titleMapper.deleteTitleById(titleId);
            if(res == 0){
                return  new MyResponseEntity<Object>(CodeNums.ERROR,"批量移出失败");
            }
        }
        return new MyResponseEntity<Object>(CodeNums.SUCCESS,"移出成功");
    }

    @Override
    public MyResponseEntity<Object> getTitleById(List<Integer> idList) {
        List<Title> titles = new ArrayList<>();
        List<Map<String, Object>> titleMaps = titleMapper.getTitleById(idList);
        Gson gson = new Gson();

        for (Map<String, Object> titleMap : titleMaps) {
            Title title = new Title();
            title.setTitleId((int) titleMap.get("title_id"));
            title.setTitleType(TitleTypeEnum.getDescriptionByCode((int)titleMap.get("title_type")));

            // 格式化时间戳为年-月-日
            Timestamp timestamp = (Timestamp) titleMap.get("title_create_stamp");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(timestamp);
            title.setTitleCreateStamp(formattedDate); // 设置为格式化后的日期字符串

            title.setTeacherId((int) titleMap.get("teacher_id"));

            String titleContentJson = (String) titleMap.get("title_content");
            TitleDTO titleDTO = gson.fromJson(titleContentJson, TitleDTO.class);

            title.setTitleContent(titleDTO);
            titles.add(title);
        }
        return new MyResponseEntity<Object>(CodeNums.SUCCESS,"",titles.size(),titles);
    }

    @Override
    public MyResponseEntity<Object> getAllTitleType() {
        List<Integer>titleList =  titleMapper.getAllTitleType();
        HashSet<String>mySet = new HashSet<String>();
        for (Integer titleType : titleList){
            mySet.add(TitleTypeEnum.getDescriptionByCode(titleType));
        }
        return new MyResponseEntity<Object>(CodeNums.SUCCESS,"",mySet.size(),mySet);
    }
}
