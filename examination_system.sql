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

 Date: 26/05/2024 16:36:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class`  (
  `class_id` int NOT NULL AUTO_INCREMENT,
  `class_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `class_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `student_numbers` int NOT NULL DEFAULT 0,
  `teacher_id` int NOT NULL,
  PRIMARY KEY (`class_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_records
-- ----------------------------
DROP TABLE IF EXISTS `exam_records`;
CREATE TABLE `exam_records`  (
  `exam_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `paper_id` int NOT NULL,
  `title_id` int NOT NULL,
  `answer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_correct` tinyint NULL DEFAULT 0,
  `scores` int NULL DEFAULT 0,
  `is_favorite` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`exam_id`) USING BTREE,
  INDEX `idx_exam_student_paper_title`(`exam_id` ASC, `student_id` ASC, `paper_id` ASC, `title_id` ASC) USING BTREE,
  INDEX `examRecordsForeignKeyPaperId`(`paper_id` ASC) USING BTREE,
  CONSTRAINT `examRecordsForeignKeyPaperId` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`paper_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 797 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `file_id` int NOT NULL AUTO_INCREMENT COMMENT '文件id\r\n\r\n',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名称\r\n',
  `file_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小kb\r\n',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '下载链接',
  `file_md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件哈希值\r\n',
  `file_is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `file_enable` tinyint(1) NULL DEFAULT 0 COMMENT '是否禁用',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paper
-- ----------------------------
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`  (
  `paper_id` int NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `paper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷名称\r\n',
  `paper_total_time` bigint NOT NULL COMMENT '试卷考试时长',
  `paper_content` json NOT NULL COMMENT '试卷内容\r\n',
  `paper_score` int NOT NULL COMMENT '试卷总分',
  `paper_create_stamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_allow_check` tinyint NOT NULL DEFAULT 0 COMMENT '是否运行查看试卷',
  `is_released` tinyint NOT NULL COMMENT '是否已经发布试卷',
  `teacher_id` int NOT NULL COMMENT '发布试卷的教师ID',
  PRIMARY KEY (`paper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for paper_class
-- ----------------------------
DROP TABLE IF EXISTS `paper_class`;
CREATE TABLE `paper_class`  (
  `paper_class_id` int NOT NULL AUTO_INCREMENT,
  `paper_id` int NOT NULL,
  `class_id` int NOT NULL,
  PRIMARY KEY (`paper_class_id`) USING BTREE,
  UNIQUE INDEX `uc_paper_class`(`class_id` ASC, `paper_id` ASC) USING BTREE,
  INDEX `paperClassForeignKeyPaperId`(`paper_id` ASC) USING BTREE,
  CONSTRAINT `paperClassForeignKeyClassId` FOREIGN KEY (`class_id`) REFERENCES `class` (`class_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `paperClassForeignKeyPaperId` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`paper_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 191 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `student_id` int NOT NULL AUTO_INCREMENT COMMENT '学生id',
  `student_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生学号',
  `student_face_id` int NOT NULL COMMENT '学生人脸，存储文件id',
  `user_id` int NOT NULL COMMENT '用户ID\r\n',
  `class_id` int NULL DEFAULT NULL COMMENT '班级ID\r\n\r\n',
  PRIMARY KEY (`student_id`) USING BTREE,
  INDEX `studentForeignKeyUserId`(`user_id` ASC) USING BTREE,
  INDEX `studentForeignKeyClassId`(`class_id` ASC) USING BTREE,
  CONSTRAINT `studentForeignKeyClassId` FOREIGN KEY (`class_id`) REFERENCES `class` (`class_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student_paper
-- ----------------------------
DROP TABLE IF EXISTS `student_paper`;
CREATE TABLE `student_paper`  (
  `student_paper_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `paper_id` int NOT NULL,
  `is_finish` tinyint(1) NULL DEFAULT 0 COMMENT '是否完成',
  `is_correct` tinyint NULL DEFAULT 0 COMMENT '是否已经批改',
  `scores` int NULL DEFAULT 0 COMMENT '得分',
  `spend_time` int NULL DEFAULT 0 COMMENT '考试花费时间',
  PRIMARY KEY (`student_paper_id`) USING BTREE,
  UNIQUE INDEX `uc_student_paper`(`student_id` ASC, `paper_id` ASC) USING BTREE,
  INDEX `studentPaperForeignKeyPaperId`(`paper_id` ASC) USING BTREE,
  CONSTRAINT `studentPaperForeignKeyPaperId` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`paper_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 349 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `teacher_id` int NOT NULL AUTO_INCREMENT COMMENT '教师id',
  `teacher_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师工号',
  `user_id` int NOT NULL COMMENT '用户id',
  PRIMARY KEY (`teacher_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for title
-- ----------------------------
DROP TABLE IF EXISTS `title`;
CREATE TABLE `title`  (
  `title_id` int NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `title_type` int NOT NULL COMMENT '题目类型',
  `title_content` json NOT NULL COMMENT '题目内容（标题、选项、答案、类型、发布教师的ID）',
  `title_create_stamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间\r\n',
  `teacher_id` int NOT NULL COMMENT '发布题目的ID',
  PRIMARY KEY (`title_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 212 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户id\r\n',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名称',
  `user_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `user_realName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户真名',
  `user_role` int NOT NULL COMMENT '用户类型，0-学生、1-教师、2-系统管理员',
  `user_avatar_id` int NULL DEFAULT NULL COMMENT '用户头像，存储文件id\r\n',
  `user_gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '男',
  `user_age` int NULL DEFAULT NULL COMMENT '年龄 ',
  `user_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `user_phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户手机号码',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Triggers structure for table student
-- ----------------------------
DROP TRIGGER IF EXISTS `decrement_student_numbers`;
delimiter ;;
CREATE TRIGGER `decrement_student_numbers` AFTER UPDATE ON `student` FOR EACH ROW BEGIN
    IF NEW.class_id IS NULL AND OLD.class_id IS NOT NULL AND (SELECT student_numbers FROM class WHERE class_id = OLD.class_id) > 0 THEN
        UPDATE class SET student_numbers = student_numbers - 1 WHERE class_id = OLD.class_id;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table student
-- ----------------------------
DROP TRIGGER IF EXISTS `increment_student_numbers`;
delimiter ;;
CREATE TRIGGER `increment_student_numbers` AFTER UPDATE ON `student` FOR EACH ROW BEGIN
    IF NEW.class_id IS NOT NULL THEN
        UPDATE class SET student_numbers = student_numbers + 1 WHERE class_id = NEW.class_id;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
