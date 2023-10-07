package com.unisinsight.business.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.dto.YnjyConfigDTO;
import com.unisinsight.business.dto.YnjyUserDTO;
import com.unisinsight.business.model.UserInfoDO;
import com.unisinsight.business.service.OrganizationService;
import com.unisinsight.framework.common.exception.BaseErrorCode;
import com.unisinsight.framework.common.exception.BaseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/biz-scene/v1/attendance/")
@Api(tags = "教育统一认证")
@Slf4j
public class LoginController {


    @Value("${url.api}")
    private String apiUrl;

    @Value("${url.web}")
    private String webUrl;
    @Autowired
    HttpServletResponse response;

    @Autowired
    OrganizationService organizationService;
    @Autowired
    YnjyConfigDTO ynjyConfigDTO;

    @SneakyThrows
    @RequestMapping("login")
    @ApiOperation("登陆")
    public void login(String code) {
        log.info("授权码登陆：" + code);
        String auth = ynjyConfigDTO.getClientId() + ":" + ynjyConfigDTO.getClientSecret();
        JSONObject ssoParam = new JSONObject();
        ssoParam.put("grant_type", "authorization_code");
        ssoParam.put("code", code);
        ssoParam.put("redirect_uri", "https://sso.ynjy.cn/51fefb9b0673440281764f1224b5bbd2/api/biz-scene/v1/attendance/login");

        String paramStr = HttpUtil.toParams(ssoParam);
        String ynjyRes = HttpRequest.post(ynjyConfigDTO.getUrl() + "/auth/oauth/token?" + paramStr)
                .header("Authorization", "Basic " + Base64.encode(auth))
                .execute().body();
        log.info("请求教育认证：" + ynjyRes);
        YnjyUserDTO ynjyUserDTO = JSONObject.parseObject(ynjyRes, YnjyUserDTO.class);

        UserInfoDO userInfo = null;
        try {
            String mobile = ynjyUserDTO.getData().getPublicUser().getPersonal().getMobile();
            userInfo = organizationService.getUserInfoByMobile(mobile);
        } catch (Exception e) {
            log.info("未查询到用户", e);
            throw BaseException.of(BaseErrorCode.USER_QUERY_ERROR.getErrorCode(), "未查询到用户");
        }
        //获取ticket
        JSONObject ticketParam = new JSONObject();
        ticketParam.put("username", userInfo.getUserCode());
        ticketParam.put("password", userInfo.getPassword());
        String ticketStr = HttpRequest.post(apiUrl + "/sso/v1/tickets")
                .header("Accept", "text/plain")
                .form(ticketParam)
                .execute().body();
        log.info("获取ticket：" + ticketStr);
        //获取cas/validate
        String casApiUrl = apiUrl + "/sso/v1/tickets/" + ticketStr;
        String casUrl = webUrl + "/cas/validate";
        String casStr = HttpRequest.post(casApiUrl)
                .header("Accept", "text/plain")
                .form("service", casUrl)
                .execute().body();
        log.info("获取cas/validate：" + casStr);
        //跳转指定连接 http://gateway.ynjy.cn:9000/cas/validate?ticket=ST-53-HTkSGNozZLI2XhwK1jdp4rLAbkcbss-sso-pod-0&last_url=http://gateway.ynjy.cn:9000/
        String loginUrl = "casUrl?ticket=casStr&last_url=webUrl";
        loginUrl = loginUrl.replace("casUrl", casUrl)
                .replace("casStr", casStr)
                .replace("webUrl", webUrl);
        log.info("跳转指定连接：" + loginUrl);
        response.sendRedirect(loginUrl);
    }
}
