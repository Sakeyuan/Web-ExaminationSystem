package com.sake.examination_system.mapper;
import com.sake.examination_system.entity.DTO.PageDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TitleMapper {
    Integer createTitle(int titleType,String titleContent,int teacherId);

    @MapKey("title_id")
    List<Map<String, Object>> getAllTitlesByTeacherId(PageDTO pageDTO);

    Integer getTitlesCountByTeacherId(PageDTO pageDTO);

    int deleteTitleById(int id);

    @MapKey("title_id")
    List<Map<String, Object>> getTitleById(List<Integer> idList);

    List<Integer> getAllTitleType();

    Integer getMaxNumTitle(int id);

    List<Integer> getTitleIds(int teacherId);
}
