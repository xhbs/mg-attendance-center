package com.unisinsight.business.rpc;

import com.unisinsight.business.dto.response.UserOrgListResDTO;
import com.unisinsight.business.rpc.dto.RoleInfo;
import com.unisinsight.business.rpc.dto.UserDetailResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "bss-uuv", url = "${service.bss-uuv.base-url}", path = "/api/infra-uuv/v0.1")
public interface InfraClient {
    /**
     * 获取用户详情（通过用户code获取)
     *
     * @param userCode 用户code
     * @return 用户详情
     */
    @GetMapping("/users/{user_code}")
    UserDetailResDTO getUserDetail(@PathVariable(value = "user_code") String userCode);

    /**
     * 获取用户的角色列表
     *
     * @param userCode 用户编号
     * @return 角色列表
     */
    @GetMapping("/users/{userCode}/roles")
    List<RoleInfo> getRoles(@PathVariable("userCode") String userCode);

    /**
     * 获取用户绑定的班级列表
     *
     * @param userCode 用户编号
     * @return 班级列表
     */
    @GetMapping("/users/{user_code}/classes")
    UserOrgListResDTO getClasses(@PathVariable(value = "user_code") String userCode);
}
