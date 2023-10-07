package com.unisinsight.business.common.enums;

import lombok.Getter;

/**
 * 管理员角色枚举
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/17
 * @since 1.0
 */
@Getter
public enum AdminRole {
    ROOT("admin", (short) 0, "省超级管理员"),
    PROVINCE("provincial_admin", (short) 1, "省级资助管理员"),
    CITY("city_admin", (short) 2, "市级资助管理员"),
    COUNTY("county_admin", (short) 3, "县级资助管理员"),
    SCHOOL("school_admin", (short) 4, "校级资助管理员"),
    HEAD_TEACHER("head_teacher", (short) 5, "班主任"),
    ;

    /**
     * 角色编号
     */
    private final String code;

    /**
     * 管理员等级
     */
    private final short level;

    /**
     * 管理员名称
     */
    private final String name;

    AdminRole(String code, short level, String name) {
        this.code = code;
        this.level = level;
        this.name = name;
    }

    /**
     * 根据编号获取枚举
     */
    public static AdminRole valueOfCode(String code) {
        AdminRole[] roles = AdminRole.values();
        for (AdminRole role : roles) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 获取上级管理员角色
     */
    public static short getAdminLevel(String code) {
        AdminRole value = valueOfCode(code);
        return value != null ? value.getLevel() : -1;
    }
}
