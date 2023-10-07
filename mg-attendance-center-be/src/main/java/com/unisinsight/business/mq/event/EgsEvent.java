package com.unisinsight.business.mq.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门禁事件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
@Data
public class EgsEvent {

    /**
     * 事件类型
     */
    @ApiModelProperty(name = "eventType", value = "事件类型")
    private Short eventType;

    /**
     * 持卡人照片
     */
    @ApiModelProperty(name = "cardUserImgUrl", value = "持卡人照片")
    private String cardUserImgUrl;

    /**
     * 上传时间
     */
    @ApiModelProperty(name = "uploadTime", value = "上传时间")
    private Long uploadTime;

    /**
     * 通道编码
     */
    @ApiModelProperty(name = "passagewayCode", value = "通道编码")
    private String passagewayCode;

    /**
     * 人员编号
     */
    @ApiModelProperty(name = "personCode", value = "人员编号")
    private String personCode;
}
