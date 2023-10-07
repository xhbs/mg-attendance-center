package com.unisinsight.business.common.enums;

/**
 * 考勤事件类型
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
public enum AttendanceEventType {
    /**
     * 签到
     */
    CHECK_IN((short)1),
    /**
     * 签退
     */
    CHECK_OUT((short)2),
    /**
     * 签到/签退
     */
    IN_OR_OUT((short) 3);

    private short value;

    AttendanceEventType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
