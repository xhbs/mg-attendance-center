package com.unisinsight.business.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ziguang.object-manage")
public class ObjectManageDataSourceConf {

    private String driverClassName;

    private String jdbcUrl;

    private String username;

    private String password;

}
