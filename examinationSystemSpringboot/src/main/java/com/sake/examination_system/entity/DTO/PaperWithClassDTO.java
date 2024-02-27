package com.sake.examination_system.entity.DTO;

import com.sake.examination_system.entity.Paper;
import com.sake.examination_system.entity.Class;
import lombok.Data;

import java.util.List;


@Data
public class PaperWithClassDTO {
    Paper paper;
    List<Class>classList;
}
