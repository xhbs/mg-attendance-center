package com.unisinsight.business.service.impl;

import cn.hutool.core.codec.Base64;
import com.unisinsight.business.common.utils.SeetaUtil;
import com.unisinsight.business.dto.request.FaceMatchReqDTO;
import com.unisinsight.business.manager.FMSManager;
import com.unisinsight.business.rpc.FaceDispositionClient;
import com.unisinsight.business.rpc.dto.ImageSimilarReqDTO;
import com.unisinsight.business.service.FaceMatchService;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

/**
 * 人脸比对服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
@Service
@Slf4j
public class FaceMatchServiceImpl implements FaceMatchService {

    /**
     * 人脸比对算法厂商
     */
    @Value("${face-macth.algorithm-vendor}")
    private String algorithmVendor;

    /**
     * 人脸比对相似度阈值
     */
    @Value("${face-macth.similarity-threshold}")
    private Integer similarityThreshold;

    @Resource
    private FaceDispositionClient faceDispositionClient;

    @Resource
    private FMSManager fmsManager;

    @Resource
    private SeetaUtil seetaUtil;

    @Value("${msg.stuSimilar}")
    private String stuSimilarMsg;

    @Override
    public void faceMatch(FaceMatchReqDTO req) throws Exception {

        boolean b = seetaUtil.liveDetection(new ByteArrayInputStream(Base64.decode(req.getCaptureImage())));
        if (b == Boolean.FALSE) {
            throw new BaseException("未检测到活体");
        }

        ImageSimilarReqDTO innerReq = new ImageSimilarReqDTO();
        innerReq.setFirstImage(fmsManager.fmsImageToBase64(req.getPersonUrl()));
        innerReq.setSecondImage(req.getCaptureImage());
        innerReq.setAlgorithmVendor(algorithmVendor);
        float similar = 0F;
        try {
            similar = faceDispositionClient.calcSimilar(innerReq);
        } catch (Exception e) {
            log.info("对比图片相似度失败", e);
            throw new BaseException(stuSimilarMsg);
        }
        if (similar <= similarityThreshold) {
            throw new BaseException("人脸相似度低于" + similarityThreshold + "%");
        }
    }
}
