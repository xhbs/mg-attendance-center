package com.unisinsight.business.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/28
 */
@Data
@AllArgsConstructor
public class AccessToken {
    private String token;
    private long expiredAt;
}
