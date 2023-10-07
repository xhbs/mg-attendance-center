package com.unisinsight.business.common.config;

import com.unisinsight.business.dto.ObjectManageDataSourceConf;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

@Configuration
public class ObjectManageJdbcConfig {

    @Resource
    private ObjectManageDataSourceConf objectManageDataSourceConf;

    @Bean
    JdbcTemplate objectManageJdbcTemplate() {
        HikariDataSource dataSourceUuv = new HikariDataSource();
        dataSourceUuv.setJdbcUrl(objectManageDataSourceConf.getJdbcUrl());
        dataSourceUuv.setDriverClassName(objectManageDataSourceConf.getDriverClassName());
        dataSourceUuv.setUsername(objectManageDataSourceConf.getUsername());
        dataSourceUuv.setPassword(objectManageDataSourceConf.getPassword());
        return new JdbcTemplate(dataSourceUuv);
    }
}
