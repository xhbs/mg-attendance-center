package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统配置项
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/11/6
 * @since 1.0
 */
@Data
public class SystemConfigDTO {
    @ApiModelProperty(value = "配置名称", required = true)
    private String key;

    @ApiModelProperty(value = "配置值", required = true)
    private String value;

    @ApiModelProperty(value = "配置描述", required = true)
    private String describe;
}