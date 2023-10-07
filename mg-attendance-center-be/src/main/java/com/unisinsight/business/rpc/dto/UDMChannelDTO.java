package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * UDM 通道信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/18
 */
@Data
public class UDMChannelDTO {

    /**
     * 通道编号
     */
    private String apeId;

    /**
     * 所属设备名称
     */
    private String ownerApsName;

    // ...
}
