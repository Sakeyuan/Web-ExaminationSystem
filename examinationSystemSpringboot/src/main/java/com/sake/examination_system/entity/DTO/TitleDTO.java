package com.sake.examination_system.entity.DTO;

import com.sake.examination_system.entity.TitleTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class TitleDTO {
    private String name;
    private int scores;
    private TitleTypeEnum type;
    private List<SelectInput> selectInput;
    private String answer;
    private int teacherId;
}
