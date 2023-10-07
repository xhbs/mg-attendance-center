package com.unisinsight.business.service;

import com.unisinsight.business.dto.request.FaceMatchReqDTO;

/**
 * 人脸比对服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
public interface FaceMatchService {

    /**
     * 人脸比对
     */
    void faceMatch(FaceMatchReqDTO req) throws Exception;
}
