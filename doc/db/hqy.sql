/*
SQLyog Trial v13.1.8 (64 bit)
MySQL - 8.0.24 : Database - hqy
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`hqy` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `hqy`;

/*Table structure for table `t_leaf_alloc` */

DROP TABLE IF EXISTS `t_leaf_alloc`;

CREATE TABLE `t_leaf_alloc` (
  `biz_tag` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '业务key,区分业务',
  `max_id` bigint NOT NULL DEFAULT '1' COMMENT '该biz_tag目前所被分配的ID号段的最大值',
  `step` int NOT NULL COMMENT '每次分配的号段长度',
  `random_step` int DEFAULT '1' COMMENT '每次getid时随机增加的长度，这样就不会有连续的id了',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci DEFAULT NULL COMMENT '描述',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间戳',
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

/*Table structure for table `t_pf_exception` */

DROP TABLE IF EXISTS `t_pf_exception`;

CREATE TABLE `t_pf_exception` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `service_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务名',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型',
  `environment` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '环境',
  `exception_class` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '异常类',
  `stack_trace` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '堆栈信息',
  `result_code` int DEFAULT NULL COMMENT '异常的状态码',
  `ip` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ip, 针对web请求才会有',
  `url` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求url, 针对web请求才会有',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_service_name` (`service_name`),
  KEY `idx_ip` (`ip`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='异常采集表';

/*Table structure for table `t_rpc_exception_record` */

DROP TABLE IF EXISTS `t_rpc_exception_record`;

CREATE TABLE `t_rpc_exception_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rpc类型 normal/slow/error',
  `application` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'provider application name.',
  `service_class_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rpc接口名',
  `method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rpc方法',
  `request_time` bigint DEFAULT NULL COMMENT '请求时间戳',
  `elapsed` bigint DEFAULT NULL COMMENT '耗时',
  `message` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '错误消息',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_application` (`application`),
  KEY `idx_service_class_name` (`service_class_name`),
  KEY `idx_method` (`method`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='rpc异常记录表';

/*Table structure for table `t_rpc_flow_record` */

DROP TABLE IF EXISTS `t_rpc_flow_record`;

CREATE TABLE `t_rpc_flow_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `caller` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rpc调用者',
  `provider` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rpc提供者',
  `method_detail` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rpc方法的计数map json',
  `service_detail` varchar(1536) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rpc接口的计数map json',
  `failure` int DEFAULT NULL COMMENT '调用失败的次数',
  `success` int DEFAULT NULL COMMENT '调用成功的次数',
  `total` int DEFAULT NULL COMMENT '调用总次数',
  `interval` bigint DEFAULT NULL COMMENT '时间窗口，毫秒',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='rpc调用记录 分钟级别的';

/*Table structure for table `t_snowflake_config` */

DROP TABLE IF EXISTS `t_snowflake_config`;

CREATE TABLE `t_snowflake_config` (
  `key` varchar(128) NOT NULL COMMENT 'serviceName#ip#port#env',
  `worker_id` int NOT NULL COMMENT '雪花id的对应服务的机器吗',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `env` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '当前环境 同一环境下workerId不能重复',
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `udx_worker_id_env` (`worker_id`,`env`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `t_throttle_block_history` */

DROP TABLE IF EXISTS `t_throttle_block_history`;

CREATE TABLE `t_throttle_block_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `throttle_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '被什么方式节流的',
  `ip` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求的客户端ip',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求url',
  `env` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '环境',
  `access_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'request json',
  `blocked_seconds` int DEFAULT NULL COMMENT '封禁时间 单位s',
  `app_name` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求的项目名',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='throttle节流器历史表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
