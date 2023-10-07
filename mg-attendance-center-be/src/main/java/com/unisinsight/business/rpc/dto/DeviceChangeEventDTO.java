package com.unisinsight.business.rpc.dto;

import lombok.Data;

import java.util.List;

/**
 * description
 *
 * @author daisike [dai.sike@unisinsight.com]
 * @date 2020/09/29 11:01
 * @since 1.0
 */
@Data
public class DeviceChangeEventDTO {

    private String action;

    private List<DeviceChangeData> data;

    /**
     * 设备类型，卡口5；通道:1
     */
    private String resourceType;


}
