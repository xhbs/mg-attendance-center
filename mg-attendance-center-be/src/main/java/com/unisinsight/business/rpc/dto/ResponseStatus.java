package com.unisinsight.business.rpc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * MDA-1400 接口响应状态参数
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
@Data
public class ResponseStatus {

    /**
     * 对应操作的url
     */
    @JsonProperty("RequestURL")
    private String requestURL;

    /**
     * 0 正常
     * 1 未知错误
     * 2 繁忙
     * 3 错误
     * 4 无效操作
     * 5 无效的XML格式
     * 6 无效的XML内容
     * 7 JSON格式无效
     * 8 JSON内容无效
     * 9 系统重启中
     */
    @JsonProperty("StatusCode")
    private String statusCode;

    /**
     * 操作响应说明
     */
    @JsonProperty("StatusString")
    private String statusString;

    /**
     * POST方法创建资源时返回的ID
     */
    @JsonProperty("Id")
    private String id;

    /**
     * 被注册方的系统时间，格式YYYYMMDDhhmmss
     */
    @JsonProperty("LocalTime")
    private String localTime;
}
