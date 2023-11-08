/*
SQLyog Trial v13.1.8 (64 bit)
MySQL - 8.0.24 : Database - apps_blog
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`apps_blog` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `apps_blog`;

/*Table structure for table `t_article` */

DROP TABLE IF EXISTS `t_article`;

CREATE TABLE `t_article` (
  `id` bigint NOT NULL COMMENT 'id',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
  `intro` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '内容',
  `type` int DEFAULT NULL COMMENT '类型',
  `background_music` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '背景音乐',
  `background_music_name` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '背景音乐名',
  `author` bigint NOT NULL COMMENT '作者',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_author` (`author`),
  KEY `idx_type` (`type`),
  KEY `idx_created` (`created`),
  FULLTEXT KEY `idx_content` (`content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客表';

/*Table structure for table `t_article_liked` */

DROP TABLE IF EXISTS `t_article_liked`;

CREATE TABLE `t_article_liked` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL COMMENT '文章id',
  `account_id` bigint NOT NULL COMMENT '点赞用户id',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_article_account_id` (`article_id`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `t_article_tags` */

DROP TABLE IF EXISTS `t_article_tags`;

CREATE TABLE `t_article_tags` (
  `article_id` bigint NOT NULL COMMENT '博客id',
  `tags_id` int NOT NULL COMMENT '标签id',
  KEY `idx_tags_id` (`tags_id`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客标签中间表';

/*Table structure for table `t_article_user_state` */

DROP TABLE IF EXISTS `t_article_user_state`;

CREATE TABLE `t_article_user_state` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `article_id` bigint NOT NULL COMMENT 't_article表id',
  `account_id` bigint DEFAULT NULL COMMENT '用户id',
  `state` int NOT NULL DEFAULT '0' COMMENT '状态值 0-表示',
  `created` datetime NOT NULL COMMENT '创建时间',
  `updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_articleId_accountId` (`article_id`,`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `t_comment` */

DROP TABLE IF EXISTS `t_comment`;

CREATE TABLE `t_comment` (
  `id` bigint NOT NULL COMMENT 'id',
  `article_id` bigint DEFAULT NULL COMMENT '博客id',
  `commenter` bigint DEFAULT NULL COMMENT '评论者id，即当前评论的发起者',
  `replier` bigint DEFAULT NULL COMMENT '如果是二级评论，则表示回复谁？',
  `content` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内容',
  `level` tinyint(1) DEFAULT NULL COMMENT '1 or 2',
  `parent` bigint DEFAULT NULL COMMENT '如果Level=2, 则此字段不能为空',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_created` (`created`),
  KEY `idx_level` (`level`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客评论表';

/*Table structure for table `t_config` */

DROP TABLE IF EXISTS `t_config`;

CREATE TABLE `t_config` (
  `id` int NOT NULL DEFAULT '1' COMMENT 'id',
  `about_me` mediumblob COMMENT '关于我',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `t_statistics` */

DROP TABLE IF EXISTS `t_statistics`;

CREATE TABLE `t_statistics` (
  `id` bigint NOT NULL COMMENT '文章id',
  `visits` int DEFAULT NULL COMMENT '访问数',
  `likes` int DEFAULT NULL COMMENT '点赞数',
  `comments` int DEFAULT NULL COMMENT '评论数',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `t_tags` */

DROP TABLE IF EXISTS `t_tags`;

CREATE TABLE `t_tags` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `t_type` */

DROP TABLE IF EXISTS `t_type`;

CREATE TABLE `t_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型名',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客类型表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
