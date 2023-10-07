package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 抽查任务状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Getter
public enum SpotCheckTaskState {
    NOT_BEGIN(1, "未开始"),
    RUNNING(2, "进行中"),
    FINISHED(3, "已结束"),
    ;

    private final int value;
    private final String name;

    SpotCheckTaskState(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameOfValue(short value) {
        for (SpotCheckTaskState status : SpotCheckTaskState.values()) {
            if (status.value == value) {
                return status.getName();
            }
        }
        return null;
    }
}
