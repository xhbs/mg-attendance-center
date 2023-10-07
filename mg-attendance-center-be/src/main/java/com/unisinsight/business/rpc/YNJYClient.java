package com.unisinsight.business.rpc;

import com.unisinsight.business.rpc.dto.YNJYSendSmsResDTO;
import com.unisinsight.business.rpc.dto.YNJYTokenResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 云南教育云服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/28
 */
@FeignClient(name = "ynjy-client", url = "${service.ynjy.base-url}")
public interface YNJYClient {

    /**
     * 获取token
     */
    @PostMapping("/auth/oauth/token?grant_type=client_credentials")
    YNJYTokenResDTO getToken(@RequestHeader("Authorization") String basic);

    /**
     * 删除token
     */
    @PostMapping("/auth/oauth2/client/logout")
    void deleteToken(@RequestParam("access_token") String token);

    /**
     * 发送短信
     */
    @PostMapping(path = "/ynedu-company/sendSms/send")
    YNJYSendSmsResDTO sendSms(@RequestHeader("Authorization") String bearer,
                              @RequestParam(value = "header", defaultValue = "云南教育") String header,
                              @RequestParam("mobile") String mobile,
                              @RequestParam("content") String message);
}
