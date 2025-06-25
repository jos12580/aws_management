/*
 Navicat Premium Dump SQL

 Source Server         : 101.37.85.93
 Source Server Type    : MySQL
 Source Server Version : 50734 (5.7.34-log)
 Source Host           : 101.37.85.93:3306
 Source Schema         : aws_light

 Target Server Type    : MySQL
 Target Server Version : 50734 (5.7.34-log)
 File Encoding         : 65001

 Date: 13/05/2025 22:30:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for aws_key_config
-- ----------------------------
DROP TABLE IF EXISTS `aws_key_config`;
CREATE TABLE `aws_key_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` int(11) NOT NULL DEFAULT '0',
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  `username` varchar(255) DEFAULT NULL,
  `access_key` varchar(255) DEFAULT NULL,
  `secret_key` varchar(255) DEFAULT '',
  `login_name` varchar(255) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='亚马逊账号配置';



-- ----------------------------
-- Table structure for light_instance
-- ----------------------------
DROP TABLE IF EXISTS `light_instance`;
CREATE TABLE `light_instance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` bigint(20) DEFAULT NULL,
  `instance_name` varchar(255) DEFAULT NULL,
  `deleted` int(4) DEFAULT '0',
  `ip` varchar(50) DEFAULT NULL,
  `sta` tinyint(4) DEFAULT NULL,
  `region` varchar(50) DEFAULT NULL,
  `region_desc` varchar(50) DEFAULT NULL,
  `error` longtext,
  `info` longtext,
  `last_used_time` bigint(20) DEFAULT NULL,
  `use_count` int(10) DEFAULT NULL,
  `devicename` varchar(255) DEFAULT NULL,
  `net_can_used` tinyint(4) DEFAULT NULL,
  `instance_type` tinyint(4) DEFAULT NULL,
  `aws_key_config_id` bigint(20) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `aws_key_config_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60007 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) NOT NULL DEFAULT '',
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  `account` varchar(50) NOT NULL DEFAULT '',
  `password` varchar(50) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0',
  `showed` int(11) NOT NULL DEFAULT '0',
  `remark` varchar(50) NOT NULL DEFAULT '',
  `username` varchar(255) DEFAULT NULL,
  `max_dv_num` int(10) DEFAULT NULL COMMENT '最大设备数',
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `user_id`, `create_time`, `account`, `password`, `status`, `showed`, `remark`, `username`, `max_dv_num`, `role`) VALUES (17, 'admin', 0, 'admin', 'admin', 0, 0, '', 'admin', 1000, 'admin');
COMMIT;

-- ----------------------------
-- Table structure for user_config
-- ----------------------------
DROP TABLE IF EXISTS `user_config`;
CREATE TABLE `user_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `map` text,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户配置表';


SET FOREIGN_KEY_CHECKS = 1;
