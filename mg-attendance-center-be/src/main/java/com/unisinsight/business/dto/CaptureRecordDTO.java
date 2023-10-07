package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 抓拍记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/17
 */
@Data
public class CaptureRecordDTO {
    @ApiModelProperty(name = "capture_image", value = "抓拍图片")
    private String captureImage;

    @ApiModelProperty(name = "captured_at", value = "抓拍时间,yyyy-MM-dd HH:mm:ss", required = true)
    private String capturedAt;

    @ApiModelProperty(name = "channel_id", value = "通道ID", hidden = true)
    private String channelId;

    @ApiModelProperty(name = "device_name", value = "抓拍相机")
    private String deviceName;
}
