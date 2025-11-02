-- ============================================
-- AI 旅行规划师数据库建表语句
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `travel_planner` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `travel_planner`;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引',
    UNIQUE KEY `uk_email` (`email`) COMMENT '邮箱唯一索引',
    KEY `idx_phone` (`phone`) COMMENT '手机号索引',
    KEY `idx_deleted` (`deleted`) COMMENT '删除状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 说明：
-- 1. 所有表都使用 utf8mb4 字符集，支持emoji等特殊字符
-- 2. 所有表都包含 deleted 字段用于逻辑删除
-- 3. 所有表都包含 create_time 和 update_time 字段用于记录时间
-- 4. 密码字段存储的是加密后的值
-- ============================================
