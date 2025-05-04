/*
 Navicat Premium Dump SQL

 Source Server         : MySQL80
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : attendancesys_db

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 04/05/2025 07:27:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456');

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `attendance_date` date NOT NULL,
  `check_in` time NULL DEFAULT NULL,
  `check_out` time NULL DEFAULT NULL,
  `status` enum('Present','Absent','Late','On Leave') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Present',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `employee_id`(`employee_id` ASC, `attendance_date` ASC) USING BTREE,
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic STATS_AUTO_RECALC = 0;

-- ----------------------------
-- Records of attendance
-- ----------------------------
INSERT INTO `attendance` VALUES (1, 'E001', '2025-05-02', '08:55:00', '17:00:00', 'Present');
INSERT INTO `attendance` VALUES (2, 'E002', '2025-05-02', '09:10:00', '18:00:00', 'Present');
INSERT INTO `attendance` VALUES (3, 'E003', '2025-05-02', '08:45:00', '17:10:00', 'Present');
INSERT INTO `attendance` VALUES (4, 'E004', '2025-05-04', '12:05:12', '05:05:12', 'On Leave');
INSERT INTO `attendance` VALUES (5, 'E005', '2025-05-04', '07:14:57', NULL, 'Present');
INSERT INTO `attendance` VALUES (6, 'E002', '2025-05-04', NULL, NULL, 'Present');
INSERT INTO `attendance` VALUES (7, 'E006', '2025-05-04', NULL, NULL, 'Present');

-- ----------------------------
-- Table structure for employees
-- ----------------------------
DROP TABLE IF EXISTS `employees`;
CREATE TABLE `employees`  (
  `employee_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `mobile_number` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `barcode_filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employees
-- ----------------------------
INSERT INTO `employees` VALUES ('E001', 'Alice Perera', 'HR', '123 Flower Rd', 'Western', 'Colombo', '0771234567', 'alice@company.com', '');
INSERT INTO `employees` VALUES ('E002', 'Bob Fernando Update', 'IT', '456 Ocean Ave', 'Southern', 'Galle', '0715998862', 'Southern', 'emp_E002_Bob_Fernando.png');
INSERT INTO `employees` VALUES ('E003', 'Chathura Silva', 'Finance', '789 Lake Rd', 'Central', 'Kandy', '0759876543', 'chathura@company.com', '');
INSERT INTO `employees` VALUES ('E004', 'Dilani Gunasekara', 'IT', '101 Hill St', 'Western', 'Negombo', '0764567890', 'dilani@company.com', 'emp_E004_Dilani_Gunasekara.png');
INSERT INTO `employees` VALUES ('E005', 'Eranga Jayasuriya', 'HR', '222 Temple Ln', 'North Western', 'Kurunegala', '0785678901', 'eranga@company.com', 'emp_E005_Eranga_Jayasuriya.png');
INSERT INTO `employees` VALUES ('E006', 'Chandupa Jayalath', 'SE', 'Anuradhapura', 'Anuradhapura', 'Anuradhapura', '0715221982', 'Chandupa@mail.com', 'emp_E006_Chandupa_Jayalath.png');

SET FOREIGN_KEY_CHECKS = 1;
