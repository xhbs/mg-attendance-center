package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 学校下设备的通道
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/17
 */
@Data
public class ChannelOfSchoolBO {

    /**
     * 通道ID
     */
    private String channelId;

    /**
     * 人员组织树编码
     */
    private String userOrgIndex;
}
