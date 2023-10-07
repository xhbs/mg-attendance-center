package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 请假类型
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Getter
public enum LeaveType {
    SICK(1, "病假"),
    PRIVATE(2, "事假"),
    OTHER(99, "其他"),
    ;

    private final int value;
    private final String name;

    LeaveType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameOfValue(int value) {
        for (LeaveType status : LeaveType.values()) {
            if (status.value == value) {
                return status.getName();
            }
        }
        return null;
    }
}
