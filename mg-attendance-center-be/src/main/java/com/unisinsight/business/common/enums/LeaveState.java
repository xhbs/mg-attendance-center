package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 请假状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Getter
public enum LeaveState {
    NOT_BEGIN(1, "请假待生效"),
    LEAVING(2, "请假中"),
    FINISHED(3, "请假结束"),
    ;

    private final int value;
    private final String name;

    LeaveState(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameOfValue(int value) {
        for (LeaveState status : LeaveState.values()) {
            if (status.value == value) {
                return status.getName();
            }
        }
        return null;
    }
}
