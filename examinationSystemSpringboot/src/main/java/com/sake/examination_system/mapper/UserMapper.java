package com.sake.examination_system.mapper;
import com.sake.examination_system.entity.User;
import com.sake.examination_system.entity.myFile;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    Integer getStudentsByUserRealNameTotal(String userRealName, List<Integer> classIds);

    int addUser(User user);

    int deleteUserById(int userId);

    User getUserByPhone(String userPhone);

    Integer checkPhoneIsExist(String userPhone);

    User getUserById(int userId);

    String getAvatarByUserId(int userId);

    void updateUserInfo(User user);

    Integer getTotalByEmail(String email);

    void setPasswordByEmail(String email, String password);
}

