package com.sake.examination_system.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class User {
    //用户ID
    @TableId(type = IdType.AUTO)
    private int userId;
    //用户名称
    private String userName;
    //用户密码
    private String userPassword;
    //用户真名
    private String userRealName;
    //用户角色
    private int userRole;
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
