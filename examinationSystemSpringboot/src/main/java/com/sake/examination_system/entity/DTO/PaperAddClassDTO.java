package com.sake.examination_system.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class PaperAddClassDTO {
    private int paperId;
    private List<String>classList;
}
