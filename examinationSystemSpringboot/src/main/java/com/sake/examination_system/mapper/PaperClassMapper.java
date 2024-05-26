package com.sake.examination_system.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface PaperClassMapper {
    void addOne(int paperId, Integer classId);

    List<HashMap<String, Integer>> getListByTeacherId(int teacherId);

    List<Integer> getClassIds(int paperId);

    List<Integer> getPaperIdsByClassId(Integer classId);
}
