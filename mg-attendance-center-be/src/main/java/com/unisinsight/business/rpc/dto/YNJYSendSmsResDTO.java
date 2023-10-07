package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * 发送短信 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/28
 */
@Data
public class YNJYSendSmsResDTO {
    private Integer code;
    private DataBean data;

    @Data
    public static class DataBean {
        private Long id;
    }
}
