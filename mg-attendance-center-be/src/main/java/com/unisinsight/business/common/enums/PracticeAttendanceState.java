package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 实习点名状态枚举
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
@Getter
public enum PracticeAttendanceState {
    RUNNING(0, "进行中"),
    PASS(1, "点名通过"),
    FAILED(2, "点名失败"),
    ;

    private final int value;
    private final String name;

    PracticeAttendanceState(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
