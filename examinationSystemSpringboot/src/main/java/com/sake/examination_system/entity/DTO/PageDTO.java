package com.sake.examination_system.entity.DTO;

import com.sake.examination_system.entity.TitleTypeEnum;
import lombok.Data;

@Data
public class PageDTO {
    private int pageNum;
    private int pageSize;
    private int id;
    private String name;
    private String titleType;
    private String className;
    private String paperName;
    private String studentName;

    public int getTitleType() {
        return TitleTypeEnum.getCodeByDescription(titleType);
    }

    // 自定义构造函数
    public PageDTO(int pageNum, int pageSize, int id, String name) {
        this.pageNum = (pageNum - 1) * pageSize;
        this.pageSize = pageSize;
        this.id = id;
        this.name = name;
    }

    public PageDTO(int id) {
        this.pageNum = 0;
        this.pageSize = 0;
        this.id = id;
        this.name = null;
        this.titleType = null;
    }
}
