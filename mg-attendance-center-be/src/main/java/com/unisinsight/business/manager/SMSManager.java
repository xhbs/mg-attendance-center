package com.unisinsight.business.manager;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.bo.AccessToken;
import com.unisinsight.business.mapper.SmsRecordMapper;
import com.unisinsight.business.model.SmsRecordDO;
import com.unisinsight.business.rpc.YNJYClient;
import com.unisinsight.business.rpc.dto.YNJYSendSmsResDTO;
import com.unisinsight.business.rpc.dto.YNJYTokenResDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

/**
 * 短信发送服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/10
 */
@Component
@Slf4j
public class SMSManager {

    @Resource
    private SmsRecordMapper smsRecordMapper;

    @Value("${ynjy.client_id}")
    private String clientId;

    @Value("${ynjy.client_secret}")
    private String clientSecret;

    @Resource
    private YNJYClient ynjyClient;

    /**
     * 教育云授权token
     */
    private volatile AccessToken token;

    /**
     * 发送短信
     *
     * @param phone   手机号码
     * @param message 短信内容
     */
    public void send(String phone, String message) {
        ensureToken();

        if (token == null) {
            saveSmsRecord(phone, message, false);
            return;
        }

        boolean success = false;
        try {
            String bearer = "Bearer " + token.getToken();

            Map<String, Object> param = new HashedMap<>();
            param.put("header", "云南教育");
            param.put("mobile", phone);
            param.put("content", message);

            String body = HttpUtil.createPost("https://ssoauth.ynjy.cn" + "/ynedu-company/sendSms/send")
                    .header("Authorization", bearer)
                    .form(param)
                    .execute()
                    .body();
            log.info("给{}发送短信{}", phone, message);
            log.info("发送短信返回结果:{}", body);
//            YNJYSendSmsResDTO res = ynjyClient.sendSms(bearer, "云南教育", phone, message);
//            log.info("发送短信结果：{}", res);
            YNJYSendSmsResDTO res = JSONObject.parseObject(body, YNJYSendSmsResDTO.class);

            success = (res != null && res.getCode() != null && res.getCode() == 0);
        } catch (Exception e) {
            log.error("发送短信失败：{}", e.getMessage());
        }

        saveSmsRecord(phone, message, success);
    }

    /**
     * 发送短信
     *
     * @param phone  手机号码
     * @param tpl    短信模板
     * @param params 模板参数
     */
    public void send(String phone, String tpl, Map<String, String> params) {
        String message = tpl;
        for (String key : params.keySet()) {
            message = message.replaceAll("\\{" + key + "\\}", params.get(key));
        }
        send(phone, message);
    }

    /**
     * 异步发送
     *
     * @param phone   手机号码
     * @param message 短信内容
     */
    @Async
    public void sendAsync(String phone, String message) {
        send(phone, message);
    }

    /**
     * 异步发送
     *
     * @param phone  手机号码
     * @param tpl    短信模板
     * @param params 模板参数
     */
    @Async
    public void sendAsync(String phone, String tpl, Map<String, String> params) {
        send(phone, tpl, params);
    }

    /**
     * 保存短信发送记录
     *
     * @param phone   手机号码
     * @param message 短信内容
     * @param success 是否发送成功
     */
    private void saveSmsRecord(String phone, String message, boolean success) {
        SmsRecordDO record = new SmsRecordDO();
        record.setPhone(phone);
        record.setMessage(message);
        record.setSuccess(success);
        record.setCreatedAt(LocalDateTime.now());
        smsRecordMapper.insertUseGeneratedKeys(record);
        log.info("[发送短信] - phone: {}, record: {}, success: {}", phone, record.getId(), success);
    }

    private void ensureToken() {
        if (token == null) {
            log.info("token为空，获取token");
            getToken();
            return;
        }

        if (System.currentTimeMillis() > token.getExpiredAt()) {
            log.info("token已过期，重新获取token");
            deleteToken();
            getToken();
        }
    }

    private synchronized void getToken() {
        if (token != null) {
            return;
        }

        String client = clientId + ":" + clientSecret;
        String basic = "Basic " + Base64.getEncoder().encodeToString(client.getBytes());

        YNJYTokenResDTO res;
        try {
            res = ynjyClient.getToken(basic);
        } catch (Exception e) {
            log.error("获取教育云token失败", e);
            return;
        }

        if (res.getCode() == null || res.getCode() != 0) {
            log.error("获取教育云token失败: {}", res);
            return;
        }

        YNJYTokenResDTO.DataBean data = res.getData();
        if (data == null || data.getAccessToken() == null) {
            log.error("获取教育云token失败: {}", res);
            return;
        }

        log.info("获取到token: {}", data);
        token = new AccessToken(data.getAccessToken(),
                System.currentTimeMillis() + ((data.getExpiresIn() - 60) * 1000));
    }

    private synchronized void deleteToken() {
        if (token == null) {
            return;
        }

        try {
            ynjyClient.deleteToken(token.getToken());
        } catch (Exception e) {
            log.error("删除token失败", e);
        }
        token = null;
    }
}
