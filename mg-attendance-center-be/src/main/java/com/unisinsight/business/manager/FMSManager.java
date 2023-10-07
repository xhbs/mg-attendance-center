package com.unisinsight.business.manager;

import com.unisinsight.business.common.utils.Base64Utils;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.Base64FileReqDTO;
import com.unisinsight.business.dto.response.Base64FileResDTO;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * mg-fms文件服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/30
 * @since 1.0
 */
@Component
@Slf4j
public class FMSManager {

    @Value("${service.mg-fms.base-url}:${service.mg-fms.register-port}")
    private String fmsRegisterUrl;

    @Value("${service.mg-fms.base-url}:${service.mg-fms.visit-port}")
    private String fmsVisitUrl;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 获取fms文件的绝对路径
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public String getAbsoluteUrl(String path) {
        return fmsVisitUrl + path;
    }

    /**
     * 上传图片base64获取相对路径
     *
     * @param base64 图片base64编码
     * @return 文件相对路径 例 /file/mg/2020/1228/20201228093617292.jpg
     */
    public String uploadImageBase64(String base64) {
        Base64FileReqDTO req = new Base64FileReqDTO(base64);
        try {
            Results<Base64FileResDTO> resp = restTemplate.exchange(fmsRegisterUrl + "/api/mg/v2/fms/files/upload/base64",
                    HttpMethod.POST, new HttpEntity<>(req),
                    new ParameterizedTypeReference<Results<Base64FileResDTO>>() {
                    }).getBody();
            assert resp != null;
            return resp.getData().getFileUrl();
        } catch (Exception e) {
            log.error("", e);
            throw new BaseException("保存图片失败");
        }
    }

    /**
     * 图片相对路径转base64
     *
     * @param fileUrl 文件相对路径 例 /file/mg/2020/1228/20201228093617292.jpg
     * @return 图片base64编码
     */
    public String fmsImageToBase64(String fileUrl) {
        return Base64Utils.imageUrlToBase64(fmsVisitUrl + fileUrl);
    }
}
