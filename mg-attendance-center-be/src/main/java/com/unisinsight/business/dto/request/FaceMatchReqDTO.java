package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 人脸比对 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/30
 * @since 1.0
 */
@Data
public class FaceMatchReqDTO {

    @ApiModelProperty(value = "原始人脸照片Url，fms图片相对路径", required = true)
    @NotNull
    private String personUrl;

    @ApiModelProperty(value = "拍摄照片，base64编码", required = true)
    @NotNull
    private String captureImage;
}
