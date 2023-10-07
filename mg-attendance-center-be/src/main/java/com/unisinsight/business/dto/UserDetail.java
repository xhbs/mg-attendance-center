package com.unisinsight.business.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : mg-attendance-center
 * @Description : 用户详情
 * @Author : xiehb
 * @Date: 2022/11/08 09:29
 * @Version 1.0.0
 */
@Data
public class UserDetail {
    private String userCode;
    private String userName;
    private String orgIndex;
    private String orgName;
    private List<Role> roles = new ArrayList<>();

    @Data
    public static class Role {
        private Integer roleId;
        private String roleCode;
        private String roleName;
    }
}
