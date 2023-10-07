package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 原始数据状态枚举
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
@Getter
public enum OriginalRecordStatus {
    UNPROCESSED((short) 0, "未处理"),
    PROCESSED((short) 1, "已处理"),
    ;

    private final short value;
    private final String name;

    OriginalRecordStatus(short value, String name) {
        this.value = value;
        this.name = name;
    }

}
