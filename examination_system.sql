/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : localhost:3306
 Source Schema         : examination_system

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 02/03/2024 10:57:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class`  (
  `class_id` int(0) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `class_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `student_numbers` int(0) NOT NULL DEFAULT 0,
  `teacher_id` int(0) NOT NULL,
  PRIMARY KEY (`class_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_records
-- ----------------------------
DROP TABLE IF EXISTS `exam_records`;
CREATE TABLE `exam_records`  (
  `exam_id` int(0) NOT NULL AUTO_INCREMENT,
  `student_id` int(0) NOT NULL,
  `paper_id` int(0) NOT NULL,
  `title_id` int(0) NOT NULL,
  `answer` varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_correct` tinyint(0) NULL DEFAULT 0,
  `scores` int(0) NULL DEFAULT 0,
  PRIMARY KEY (`exam_id`) USING BTREE,
  INDEX `idx_exam_student_paper_title`(`exam_id`, `student_id`, `paper_id`, `title_id`) USING BTREE,
  INDEX `examRecordsForeignKeyPaperId`(`paper_id`) USING BTREE,
  CONSTRAINT `examRecordsForeignKeyPaperId` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`paper_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `file_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '文件id\r\n\r\n',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名称\r\n',
  `file_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint(0) NULL DEFAULT NULL COMMENT '文件大小kb\r\n',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '下载链接',
  `file_md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件哈希值\r\n',
  `file_is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `file_enable` tinyint(1) NULL DEFAULT 0 COMMENT '是否禁用',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paper
-- ----------------------------
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`  (
  `paper_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `paper_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷名称\r\n',
  `paper_total_time` bigint(0) NOT NULL,
  `paper_content` json NOT NULL COMMENT '试卷内容\r\n',
  `paper_score` int(0) NOT NULL,
  `paper_create_stamp` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `is_allow_check` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否运行查看试卷',
  `teacher_id` int(0) NOT NULL COMMENT '发布试卷的教师ID',
  PRIMARY KEY (`paper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paper_class
-- ----------------------------
DROP TABLE IF EXISTS `paper_class`;
CREATE TABLE `paper_class`  (
  `paper_class_id` int(0) NOT NULL AUTO_INCREMENT,
  `paper_id` int(0) NOT NULL,
  `class_id` int(0) NOT NULL,
  PRIMARY KEY (`paper_class_id`) USING BTREE,
  UNIQUE INDEX `uc_paper_class`(`class_id`, `paper_id`) USING BTREE,
  CONSTRAINT `paperClassForeignKeyClassId` FOREIGN KEY (`class_id`) REFERENCES `class` (`class_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `student_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '学生id',
  `student_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生学号',
  `student_face_id` int(0) NOT NULL COMMENT '学生人脸，存储文件id',
  `user_id` int(0) NOT NULL COMMENT '用户ID\r\n',
  `class_id` int(0) NULL DEFAULT NULL COMMENT '班级ID\r\n\r\n',
  PRIMARY KEY (`student_id`) USING BTREE,
  INDEX `studentForeignKeyUserId`(`user_id`) USING BTREE,
  INDEX `studentForeignKeyClassId`(`class_id`) USING BTREE,
  CONSTRAINT `studentForeignKeyClassId` FOREIGN KEY (`class_id`) REFERENCES `class` (`class_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student_paper
-- ----------------------------
DROP TABLE IF EXISTS `student_paper`;
CREATE TABLE `student_paper`  (
  `student_paper_id` int(0) NOT NULL AUTO_INCREMENT,
  `student_id` int(0) NOT NULL,
  `paper_id` int(0) NOT NULL,
  `is_finish` tinyint(1) NULL DEFAULT 0 COMMENT '是否完成',
  `is_correct` tinyint(0) NULL DEFAULT 0 COMMENT '是否已经批改',
  `scores` int(0) NULL DEFAULT 0 COMMENT '得分',
  `spend_time` int(0) NULL DEFAULT 0 COMMENT '考试花费时间',
  PRIMARY KEY (`student_paper_id`) USING BTREE,
  UNIQUE INDEX `uc_student_paper`(`student_id`, `paper_id`) USING BTREE,
  INDEX `studentPaperForeignKeyPaperId`(`paper_id`) USING BTREE,
  CONSTRAINT `studentPaperForeignKeyPaperId` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`paper_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `teacher_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '教师id',
  `teacher_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师工号',
  `user_id` int(0) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`teacher_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for title
-- ----------------------------
DROP TABLE IF EXISTS `title`;
CREATE TABLE `title`  (
  `title_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `title_type` int(0) NOT NULL COMMENT '题目类型',
  `title_content` json NOT NULL COMMENT '题目内容（标题、选项、答案、类型、发布教师的ID）',
  `title_create_stamp` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间\r\n',
  `teacher_id` int(0) NOT NULL COMMENT '发布题目的ID',
  PRIMARY KEY (`title_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户id\r\n',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名称',
  `user_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `user_realName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户真名',
  `user_role` int(0) NOT NULL COMMENT '用户类型，0-学生、1-教师、2-系统管理员',
  `user_avatar_id` int(0) NULL DEFAULT NULL COMMENT '用户头像，存储文件id\r\n',
  `user_gender` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '男',
  `user_age` int(0) NULL DEFAULT NULL COMMENT '年龄 ',
  `user_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `user_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户手机号码',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
