package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * @desc 
 * @author  cn [cheng.nian@unisinsight.com]
 * @time    2020/11/10 18:52
 */
@Data
public class UDMDeviceDTO {

    /**
     * ip 地址
     */
    private String ipAddr;

    /**
     * 设备编号
     */
    private String apeId;

    /**
     * 厂商
     */
    private String producer;
}
