package com.unisinsight.business.common.enums;

/**
 * 调整说明
 *
 * @author zhangjin
 * @date 2020/12/28 14:44
 */
public enum AdjustModeEnum {
    /**
     * 原始考勤
     */
    ORIGINAL_RESULT(0, "原始考勤"),

    /**
     * 系统变更
     */
    SYSTEM_CHANGE(1, "系统变更"),

    /**
     * 手动变更
     */
    MANUAL_CHANGE(2, "手动变更"),

    /**
     * 申述变更
     */
    ALLEGEDLY_CHANGE(3, "申诉变更");

    private final int value;

    private final String name;

    AdjustModeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static String getNameByValue(int value) {
        for (AdjustModeEnum modeEnum : AdjustModeEnum.values()) {
            if (modeEnum.getValue() == value) {
                return modeEnum.name;
            }
        }
        return null;
    }
}
