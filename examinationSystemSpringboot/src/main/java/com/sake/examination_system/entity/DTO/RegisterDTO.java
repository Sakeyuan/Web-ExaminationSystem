package com.sake.examination_system.entity.DTO;

import lombok.Data;

@Data
public class RegisterDTO {
    private String userPhone;
    private String userPassword;
    private String number;
    private int role;
}
