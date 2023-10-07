package com.unisinsight.business.service;

import com.unisinsight.business.bo.ChannelOfSchoolBO;

import java.util.List;

/**
 * 考勤设备服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/25
 */
public interface DeviceService {

    /**
     * 获取所有学校下的通道集合
     */
    List<ChannelOfSchoolBO> findAllChannels();
}
