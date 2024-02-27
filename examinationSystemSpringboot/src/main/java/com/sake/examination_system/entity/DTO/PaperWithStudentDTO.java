package com.sake.examination_system.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sake.examination_system.entity.Paper;

public class PaperWithStudentDTO extends Paper {
    private int studentId;

    private String studentNumber;

    @TableField("user_realName")
    private String userRealName;

    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }
}
