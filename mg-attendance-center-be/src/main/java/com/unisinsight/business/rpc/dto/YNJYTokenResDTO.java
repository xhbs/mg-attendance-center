package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * 获取TOKEN 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/28
 */
@Data
public class YNJYTokenResDTO {
    private Integer code;

    private DataBean data;

    @Data
    public static class DataBean {
        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
    }
}
