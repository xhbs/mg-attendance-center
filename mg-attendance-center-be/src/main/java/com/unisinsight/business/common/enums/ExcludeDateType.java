package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 日常考勤排除日期类型
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Getter
public enum ExcludeDateType {
    HOLIDAY(1, "法定节假日"),
    CUSTOM_DATE(2, "自定义节假日"),
    ;

    private final int value;
    private final String name;

    ExcludeDateType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
