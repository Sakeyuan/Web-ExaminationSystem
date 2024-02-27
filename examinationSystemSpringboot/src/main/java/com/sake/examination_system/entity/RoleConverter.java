package com.sake.examination_system.entity;

import java.util.HashMap;
import java.util.Map;

public class RoleConverter {
    private static final Map<Integer, String> roleToString = new HashMap<>();
    private static final Map<String, Integer> stringToRole = new HashMap<>();

    static {
        // 初始化映射关系
        roleToString.put(1, "学生");
        roleToString.put(2, "教师");

        stringToRole.put("学生", 1);
        stringToRole.put("教师", 2);
    }

    public static String convertRoleToString(int role) {
        return roleToString.get(role);
    }

    public static int convertStringToRole(String roleString) {
        return stringToRole.get(roleString);
    }
}
