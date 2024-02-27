package com.sake.examination_system.entity;

public enum TitleTypeEnum {
    UNKNOWN_TITLE(0,"未知题"),
    SINGLE_TITLE(1, "单选题"),
    MULTIPLE_TITLE(2, "多选题"),
    JUDGE_TITLE(3, "判断题"),
    FILL_TITLE(4, "填空题"),
    SHORT_TITLE(5, "简答题");


    private final int code;
    private final String description;

    TitleTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (TitleTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type.getDescription();
            }
        }
        return "Unknown";
    }

    public static int getCodeByDescription(String description) {
        for (TitleTypeEnum type : values()) {
            if (type.getDescription().equals(description)) {
                return type.getCode();
            }
        }
        return -1;
    }
}
