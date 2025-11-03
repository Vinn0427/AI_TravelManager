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
-- 旅行计划表 (Plan)
-- ============================================
CREATE TABLE IF NOT EXISTS `plan` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `destination` VARCHAR(100) NOT NULL COMMENT '目的地',
    `start_date` DATE NOT NULL COMMENT '出发日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `total_days` INT(11) NOT NULL COMMENT '总天数',
    `budget` DECIMAL(10, 2) NOT NULL COMMENT '总预算（元）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
    KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
    CONSTRAINT `fk_plan_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅行计划表';

-- ============================================
-- 景点表 (Spot)
-- ============================================
CREATE TABLE IF NOT EXISTS `spot` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '景点ID',
    `plan_id` BIGINT(20) NOT NULL COMMENT '计划ID',
    `day_number` INT(11) NOT NULL COMMENT '第几天（从1开始）',
    `name` VARCHAR(200) NOT NULL COMMENT '景点名称',
    `location` VARCHAR(500) DEFAULT NULL COMMENT '位置描述',
    `latitude` DECIMAL(10, 7) DEFAULT NULL COMMENT '纬度',
    `longitude` DECIMAL(10, 7) DEFAULT NULL COMMENT '经度',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`) COMMENT '计划ID索引',
    KEY `idx_day_number` (`day_number`) COMMENT '天数索引',
    CONSTRAINT `fk_spot_plan` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- ============================================
-- 预算表 (Budget)
-- ============================================
CREATE TABLE IF NOT EXISTS `budget` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '预算ID',
    `plan_id` BIGINT(20) NOT NULL COMMENT '计划ID',
    `category` VARCHAR(50) NOT NULL COMMENT '预算类别（如：交通、住宿、餐饮、景点、购物等）',
    `amount` DECIMAL(10, 2) NOT NULL COMMENT '预算金额（元）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`) COMMENT '计划ID索引',
    KEY `idx_category` (`category`) COMMENT '类别索引',
    CONSTRAINT `fk_budget_plan` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';

-- ============================================
-- 用户偏好表 (UserPreference)
-- ============================================
CREATE TABLE IF NOT EXISTS `user_preference` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '偏好ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `travel_style` VARCHAR(100) DEFAULT NULL COMMENT '旅行风格（如：休闲、冒险、文化、购物等）',
    `budget_range` VARCHAR(100) DEFAULT NULL COMMENT '预算范围（如：经济型、中等、豪华）',
    `preferred_transport` VARCHAR(100) DEFAULT NULL COMMENT '偏好交通方式（如：飞机、高铁、自驾等）',
    `preferred_accommodation` VARCHAR(100) DEFAULT NULL COMMENT '偏好住宿类型（如：酒店、民宿、青旅等）',
    `dietary_preference` VARCHAR(200) DEFAULT NULL COMMENT '饮食偏好（如：素食、海鲜、当地特色等）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '用户ID唯一索引（一个用户只有一份偏好）',
    CONSTRAINT `fk_preference_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户偏好表';

-- ============================================
-- 每日路线指引表 (DailyGuide)
-- ============================================
CREATE TABLE IF NOT EXISTS `dailyguide` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `plan_id` BIGINT(20) NOT NULL COMMENT '计划ID',
    `day_number` INT(11) NOT NULL COMMENT '第几天（从1开始）',
    `guide_text` TEXT NOT NULL COMMENT '路线指引文本',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_day_number` (`day_number`),
    CONSTRAINT `fk_dailyguide_plan` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_dailyguide_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日路线指引表';

-- ============================================
-- 说明：
-- 1. 所有表都使用 utf8mb4 字符集，支持emoji等特殊字符
-- 2. 所有表都包含 create_time 字段用于记录时间
-- 3. 密码字段存储的是加密后的值
-- 4. 表关系：
--    - User 1:N Plan (一个用户有多个计划)
--    - Plan 1:N Spot (一个计划有多个景点)
--    - Plan 1:N Budget (一个计划有多个预算项)
--    - User 1:1 UserPreference (一个用户只有一份偏好)
-- 5. 使用外键约束保证数据完整性，级联删除
-- ============================================
