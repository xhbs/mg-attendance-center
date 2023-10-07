package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 实习状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Getter
public enum PracticeStatus {
    NOT_BEGIN((short) 1, "待实习"),
    PRACTICING((short) 2, "实习中"),
    FINISHED((short) 3, "实习结束"),
    ;

    private final short value;
    private final String name;

    PracticeStatus(short value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameOfValue(short value) {
        for (PracticeStatus status : PracticeStatus.values()) {
            if (status.value == value) {
                return status.getName();
            }
        }
        return null;
    }
}
