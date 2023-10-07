package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 申诉状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Getter
public enum ApprovalResult {
    PASS(1, "通过"),
    REFUSE(2, "拒绝"),
    ;
    private final int value;
    private final String name;

    ApprovalResult(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
