package com.unisinsight.business.rpc.dto;

import lombok.Data;

import java.util.List;

/**
 * description
 *
 * @author daisike [dai.sike@unisinsight.com]
 * @date 2020/09/29 11:02
 * @since 1.0
 */
@Data
public class DeviceChangeData {


    private String apeId;

    private String name;

    private String model;

    private String orgCode;

    private String orgName;

    private String orgIndex;

    private String accessType;

    private String resourceType;

    private String subType;

    private List<DeviceDTO> deviceList;

    private List<ChannelDTO> channelList;



}
