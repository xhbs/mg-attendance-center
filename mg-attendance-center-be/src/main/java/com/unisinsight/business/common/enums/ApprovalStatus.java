package com.unisinsight.business.common.enums;

import lombok.Getter;

import java.util.HashMap;

/**
 * 审批状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Getter
public enum ApprovalStatus {
    UN_REPORT(1, "未上报"),
    UN_PROCESS(2, "未处理"),
    PASSED(3, "同意"),
    REJECTED(4, "拒绝");

    private final int value;
    private final String name;

    ApprovalStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static HashMap<Integer, String> statusMap = new HashMap<>(5);

    static {
        for (ApprovalStatus value : ApprovalStatus.values()) {
            statusMap.put(value.getValue(), value.getName());
        }
    }

    public static String getNameOfValue(int value) {
        return statusMap.get(value);
    }
}
