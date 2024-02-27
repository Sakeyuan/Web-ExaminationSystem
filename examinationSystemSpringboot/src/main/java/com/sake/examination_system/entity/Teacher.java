package com.sake.examination_system.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Teacher {
    @TableId(type = IdType.AUTO)
    private int teacherId;
    private String teacherNumber;
    private User user;
    public int getUserId() {
        return user.getUserId();
    }
}
