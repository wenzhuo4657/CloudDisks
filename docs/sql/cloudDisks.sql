-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: 117.72.36.124    Database: cloudDisks
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


CREATE DATABASE `cloudDisks` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
--
-- Table structure for table `email_code`
--

DROP TABLE IF EXISTS `email_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_code` (
  `email` varchar(200) NOT NULL COMMENT '邮箱',
  `code` varchar(10) NOT NULL COMMENT '编号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态(0未使用 1已使用)',
  PRIMARY KEY (`email`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮箱验证码';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `email_code`
--

LOCK TABLES `email_code` WRITE;
/*!40000 ALTER TABLE `email_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `email_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_info`
--

DROP TABLE IF EXISTS `file_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_info` (
  `file_id` varchar(10) NOT NULL COMMENT '文件ID',
  `user_id` varchar(20) NOT NULL COMMENT '用户ID',
  `file_md5` varchar(32) DEFAULT NULL COMMENT '文件MD5值 用于秒传',
  `file_pid` varchar(10) DEFAULT NULL COMMENT '文件父级ID 用于多级目录',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小 单位是字节B',
  `file_name` varchar(200) DEFAULT NULL COMMENT '文件名称',
  `file_cover` varchar(100) DEFAULT NULL COMMENT '文件封面',
  `file_path` varchar(200) DEFAULT NULL COMMENT '文件路径',
  `create_time` datetime DEFAULT NULL COMMENT '文件创建时间',
  `last_update_time` datetime DEFAULT NULL COMMENT '文件最后更新时间',
  `folder_type` tinyint(1) DEFAULT NULL COMMENT '类型 0文件 1目录',
  `file_category` tinyint(1) DEFAULT NULL COMMENT '文件分类 1视频 2音频 3图片 4文档 5其它',
  `file_type` tinyint(1) DEFAULT NULL COMMENT '文件类型 1视频 2音频 3图片 4pdf 5doc 6excel 7txt 8code 9zip 10其它',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 0转码中 1转码失败 2转码成功',
  `recovery_time` datetime DEFAULT NULL COMMENT '文件被删除的时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '逻辑删除 0删除 1回收站 2正常',
  PRIMARY KEY (`file_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_info`
--

LOCK TABLES `file_info` WRITE;
/*!40000 ALTER TABLE `file_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_share`
--

DROP TABLE IF EXISTS `file_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_share` (
  `share_id` varchar(20) NOT NULL COMMENT '分享ID',
  `file_id` varchar(20) DEFAULT NULL COMMENT '文件ID',
  `user_id` varchar(20) DEFAULT NULL COMMENT '分享人ID',
  `valid_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0一天 1七天 2三十天 3永久有效',
  `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
  `share_time` datetime DEFAULT NULL COMMENT '分享时间',
  `code` varchar(10) DEFAULT NULL COMMENT '提取码',
  `show_count` int DEFAULT NULL COMMENT '浏览次数',
  PRIMARY KEY (`share_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件分享表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_share`
--

LOCK TABLES `file_share` WRITE;
/*!40000 ALTER TABLE `file_share` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_share` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `user_id` varchar(15) NOT NULL COMMENT '用户ID',
  `nick_name` varchar(20) DEFAULT NULL COMMENT '昵称',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `qq_open_id` varchar(50) DEFAULT NULL COMMENT '第三方登录id',
  `qq_avatar` varchar(200) DEFAULT NULL COMMENT 'qq头像',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `join_time` datetime DEFAULT NULL COMMENT '加入时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '用户状态(0禁用 1启用)',
  `user_space` bigint DEFAULT NULL COMMENT '用户云盘空间 byte',
  `total_space` bigint DEFAULT NULL COMMENT '总云盘空间 byte',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'cloudDisks'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-25 15:15:46
