package com.vinn.travelmanager;

import com.vinn.travelmanager.config.DataSourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * SpringBoot主启动类
 */
@SpringBootApplication
@MapperScan("com.vinn.travelmanager.mapper")
public class TravelManagerApplication {

    private static final Logger logger = LoggerFactory.getLogger(TravelManagerApplication.class);

    @Autowired
    private DataSourceConfig dataSourceConfig;

    public static void main(String[] args) {
        SpringApplication.run(TravelManagerApplication.class, args);
    }

    /**
     * 应用启动完成后打印数据库配置信息
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("=== AI 旅行规划师应用启动完成 ===");
        dataSourceConfig.logDataSourceInfo();
        logger.info("应用运行在: http://localhost:8080");
        logger.info("API文档: http://localhost:8080/api/user/*");
    }
}

