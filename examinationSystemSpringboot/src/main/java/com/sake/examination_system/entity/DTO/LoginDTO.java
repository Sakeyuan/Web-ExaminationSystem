package com.sake.examination_system.entity.DTO;

import lombok.Data;

@Data
public class LoginDTO {
    private String userPhone;
    private String userEmail;
    private String userPassword;
    private String role;
    private String token;
    private String refreshToken;
    private int userId;
    private int id;
}
