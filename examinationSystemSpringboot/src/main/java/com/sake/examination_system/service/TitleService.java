package com.sake.examination_system.service;

import com.sake.examination_system.entity.DTO.PageDTO;
import com.sake.examination_system.entity.DTO.TitleDTO;
import com.sake.examination_system.util.MyResponseEntity;

import java.util.List;


public interface TitleService {
    MyResponseEntity<Object> createTitle(TitleDTO titleDTO);

    MyResponseEntity<Object> getAllTitlesByTeacherId(PageDTO pageDTO);

    MyResponseEntity<Object> deleteTitleById(int id);

    MyResponseEntity<Object> batchDeleteTitleById(List<Integer>titleIds);

    MyResponseEntity<Object> getTitleById(List<Integer> idList);

    MyResponseEntity<Object> getAllTitleType();
}
