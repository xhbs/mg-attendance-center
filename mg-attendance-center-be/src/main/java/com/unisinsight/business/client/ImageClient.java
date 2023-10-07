package com.unisinsight.business.client;

import com.unisinsight.business.manager.FMSManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ImageClient {

    @Value("${url.dynamic-form}")
    private String dynamicFormUrl;

    @Resource
    private FMSManager fmsManager;

    public String uploadImage(String base64) {
        return fmsManager.uploadImageBase64(base64);
    }
}
