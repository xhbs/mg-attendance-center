package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * 图片相似度比对 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/30
 * @since 1.0
 */
@Data
public class ImageSimilarReqDTO {
    /**
     * 第一张图
     */
    private String firstImage;

    /**
     * 第二张图
     */
    private String secondImage;

    /**
     * 算法厂商
     */
    private String algorithmVendor;
}
