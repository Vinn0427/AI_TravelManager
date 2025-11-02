package com.vinn.travelmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * 提供数据库连接的健康检查和日志输出
 */
@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * 打印数据源配置信息（不打印密码）
     */
    public void logDataSourceInfo() {
        logger.info("=== 数据库配置信息 ===");
        logger.info("数据库URL: {}", url);
        logger.info("用户名: {}", username);
        logger.info("驱动类: {}", driverClassName);
        logger.info("=========================");
        logger.info("提示：如果连接失败，请检查：");
        logger.info("1. MySQL服务是否启动");
        logger.info("2. 数据库 travel_planner 是否存在");
        logger.info("3. 用户名和密码是否正确");
        logger.info("4. 端口号是否正确（默认3306）");
    }
}

