-- DROP DATABASE IF EXISTS `repository`;
CREATE DATABASE `repository`;
USE `repository`;

--
-- Table structure for table `plugin`
--
-- DROP TABLE IF EXISTS `repository`.`plugin`;
CREATE TABLE `repository`.`plugin` (
  `plugin_id` VARCHAR(32) NOT NULL COMMENT '插件Id',
  `name` VARCHAR(45) NOT NULL UNIQUE COMMENT '插件名称',
  `build` DATE NULL COMMENT '插件构建日期',
  `version` VARCHAR(45) NOT NULL COMMENT '插件版本号',
  `description` VARCHAR(255) NULL COMMENT '插件功能描述',
  `compatible_version` VARCHAR(45) NOT NULL COMMENT '插件前向兼容的最老版本',
  `os` VARCHAR(45) NULL COMMENT '插件依赖的操作系统最低版本',
  `arch` VARCHAR(45) NULL COMMENT '插件处理器架构，分为x86,amd64,mips,aarch64等',
  `dependency` VARCHAR(32) NULL COMMENT '插件所依赖的插件ID列表，以逗号分隔',
  `category_id` VARCHAR(32) NULL COMMENT '插件类别Id',
  `published` TINYINT(1) UNSIGNED DEFAULT 0 COMMENT '插件发布状态，0表示下架， 1表示发布',
  `deleted` TINYINT(1) UNSIGNED DEFAULT 0 COMMENT '插件删除状态, 0表示未删除， 1表示已放入回收站',
  PRIMARY KEY (`plugin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Table structure for table `category`
--
-- DROP TABLE IF EXISTS `repository`.`category`;
CREATE TABLE `repository`.`category` (
  `category_id` VARCHAR(32) NOT NULL COMMENT '类别Id',
  `name` VARCHAR(45) NOT NULL UNIQUE COMMENT '类别名称',
  `parent_id` VARCHAR(32) NULL COMMENT '类别父级Id',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;