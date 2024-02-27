package com.sake.examination_system.entity;

import lombok.Data;

import java.util.List;

@Data
public class FaceMatchResult {
    private int error_code;
    private String error_msg;
    private long log_id;
    private long timestamp;
    private int cached;
    private Result result;

    @Data
    public static class Result {
        private double score;
        private int face_num;
        private List<FaceInfo> face_list;
    }

    @Data
    public static class FaceInfo {
        private String face_token;
    }
}
