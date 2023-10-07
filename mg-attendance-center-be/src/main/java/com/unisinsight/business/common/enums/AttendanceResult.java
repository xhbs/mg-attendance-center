package com.unisinsight.business.common.enums;

import com.google.common.collect.HashBiMap;
import lombok.Getter;

/**
 * 考勤结果枚举定义
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/1/28
 * @since 1.0
 */
@Getter
public enum AttendanceResult {
    REST(-1, "休息"),
    NORMAL(0, "在校"),
    MISTAKE(1, "误报"),
    PRACTICE(2, "实习"),
    LEAVE(3, "请假"),
    APPEAL(4, "申诉通过"),
    ABSENCE(99, "缺勤"),
    ;

    private int type;
    private String name;

    AttendanceResult(int type, String typeName) {
        this.type = type;
        this.name = typeName;
    }

    private static HashBiMap<Integer, String> RESULT_MAP = HashBiMap.create(4);

    static {
        AttendanceResult[] values = values();
        for (AttendanceResult value : values) {
            RESULT_MAP.put(value.type, value.name);
        }
    }

    /**
     * 根据 name 得到 type
     */
    public static int getTypeByName(String name) {
        return RESULT_MAP.inverse().get(name);
    }

    /**
     * 根据 type 得到 name
     */
    public static String getNameByType(int type) {
        return RESULT_MAP.get(type);
    }
}
