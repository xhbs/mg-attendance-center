package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/7/13
 * @since 1.0
 */
@Getter
public enum RecordType {
    APPEAL(0, "申诉"),
    LEAVE(1, "请假"),
    PRACTICE(2, "实习");

    private final int value;
    private final String name;

    RecordType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
