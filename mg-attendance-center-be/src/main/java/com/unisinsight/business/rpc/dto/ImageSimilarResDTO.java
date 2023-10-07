package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * 图片相似度比对 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/30
 * @since 1.0
 */
@Data
public class ImageSimilarResDTO {
    /**
     * 相似度
     */
    private float similar;
}
