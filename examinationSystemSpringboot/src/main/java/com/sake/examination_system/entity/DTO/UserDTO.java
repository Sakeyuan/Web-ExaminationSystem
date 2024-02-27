package com.sake.examination_system.entity.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private int userId;
    //用户名称
    private String userName;
    //用户真名
    private String userRealName;
    //用户头像
    private int userAvatarId;
    //用户性别
    private String userGender;
    //用户年龄
    private int userAge;
    //用户邮箱
    private String userEmail;
    //用户手机
    private String userPhone;
}
