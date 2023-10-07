package com.unisinsight.business.client.uuv;

import com.unisinsight.business.client.uuv.res.RoleDetailRes;
import com.unisinsight.business.client.uuv.res.UserDetailRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: UuvClient
 * @Description: 查询用户所属部门
 * @Author: Lqj
 * @Date: 2022/2/11 9:24
 **/
@FeignClient(url = "${url.uuv}", name = "bass-uuv")
public interface UuvClient {

    /**
     * 根据用户code获取用户信息
     *
     * @param userCode
     * @return
     */
    @GetMapping("/api/infra-uuv/v0.1/users/{user_code}")
    UserDetailRes userDetail(@PathVariable("user_code") String userCode);

    /**
     * 根据角色code获取角色信息
     *
     * @param roleCode
     * @return
     */
    @GetMapping("/api/infra-uuv/v0.1/roles/{role_code}")
    RoleDetailRes roleDetail(@PathVariable("role_code") String roleCode);

}
