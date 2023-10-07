package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * 角色信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/17
 * @since 1.0
 */
@Data
public class RoleInfo {

    /**
     * 角色ID
     */
    private int roleId;

    /**
     * 角色编号
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;
}
