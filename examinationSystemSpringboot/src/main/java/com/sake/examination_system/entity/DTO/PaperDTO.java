package com.sake.examination_system.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class PaperDTO {
    private String paperName;
    private Integer examTotalTime;
    private int isAllowCheck;
    private List<Integer> selectedClasses;
    private List<SubheadingData> subheadings;
    private int teacherId;

    @Data
    public static class SubheadingData {
        private String title;
        private List<Integer> ids;
    }
}
