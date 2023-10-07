package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 组织类型 sub_type 字段
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/17
 * @since 1.0
 */
@Getter
public enum OrgType {
    ROOT((short) 0, "根节点"),
    PROVINCE((short) 1, "省"),
    CITY((short) 2, "市/州"),
    COUNTRY((short) 3, "区/县"),
    SCHOOL((short) 4, "学校"),
    CLASS((short) 5, "班级"),
    ;

    private final short value;
    private final String name;

    OrgType(short value, String name) {
        this.value = value;
        this.name = name;
    }
}
