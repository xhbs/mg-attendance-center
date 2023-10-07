package com.unisinsight.business.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ynjy-sso")
public class YnjyConfigDTO {
    private String url;
    private String clientId;
    private String clientSecret;
}
