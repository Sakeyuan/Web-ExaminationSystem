package com.sake.examination_system.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class InviteDTO {
    private String classId;
    private List<String> data;
}
