package com.sake.examination_system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sake.examination_system.entity.DTO.TitleDTO;
import lombok.Data;

@Data
public class Title {
    private int titleId;
    private String titleType;
    private TitleDTO titleContent;
    @TableField("title_create_stamp")
    private String  titleCreateStamp;
    private int teacherId;

}
