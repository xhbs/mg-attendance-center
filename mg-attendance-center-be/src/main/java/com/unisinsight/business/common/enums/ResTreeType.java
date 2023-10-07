package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 资源树类型
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/17
 * @since 1.0
 */
@Getter
public enum ResTreeType {
    DEVICE((short) 0, "设备组织"),
    PERSON((short) 1, "人员组织"),
    ;

    private final int value;
    private final String name;

    ResTreeType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
