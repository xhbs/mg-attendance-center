package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 原始数据来源枚举
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
@Getter
public enum OriginalRecordSource {
    DISPOSITION((short) 0, "布控告警"),
    EGS_EVENT((short) 1, "门禁事件"),
    IDENTITY((short) 2, "置信"),
    ;
    private final short value;
    private final String name;

    OriginalRecordSource(short value, String name) {
        this.value = value;
        this.name = name;
    }
}
