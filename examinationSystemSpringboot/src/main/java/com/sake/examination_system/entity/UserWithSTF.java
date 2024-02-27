package com.sake.examination_system.entity;
import lombok.Data;

@Data
public class UserWithSTF {
    private User user;
    private String number;
    private myFile avatar;
    private myFile face;
    private Class myClass;
}
