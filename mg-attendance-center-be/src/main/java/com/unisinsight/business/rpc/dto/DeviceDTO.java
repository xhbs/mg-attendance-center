package com.unisinsight.business.rpc.dto;

import lombok.Data;

import java.util.List;

/**
 * description
 *
 * @author daisike [dai.sike@unisinsight.com]
 * @date 2020/09/29 16:09
 * @since 1.0
 */
@Data
public class DeviceDTO {

    private List<ChannelDTO> channelList;
}
