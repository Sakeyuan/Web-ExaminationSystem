package com.sake.examination_system.entity.DTO;

import lombok.Data;

@Data
public class EmailCodeDTO {
    private String email;
    private String code;
    private String password;
}
