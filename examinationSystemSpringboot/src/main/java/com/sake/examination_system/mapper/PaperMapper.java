package com.sake.examination_system.mapper;

import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.PaperWithClassDTO;
import com.sake.examination_system.entity.Paper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaperMapper {
    Integer releasePaper(Paper paper);

    List<Paper> getAllPaperListByTeacherId(PageDTO pageDTO);

    Integer getAllPaperListByTeacherIdTotal(PageDTO pageDTO);

    Integer deletePaperById(int id);

    List<Paper>getAllPaper();

    List<Paper> getAllPaperByPage(List<Integer>paperList,PageDTO pageDTO);

    List<Paper>getPaperById(List<Integer> paperIdList);

    int getPaperCountByTeacherId(int teacherId);


    void setIsReleased(int paperId);
}
