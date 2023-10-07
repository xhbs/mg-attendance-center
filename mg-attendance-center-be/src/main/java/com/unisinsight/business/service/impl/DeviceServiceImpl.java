package com.unisinsight.business.service.impl;

import com.unisinsight.business.bo.ChannelOfSchoolBO;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 考勤设备服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/25
 */
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    @Value("${dbServer.ip}")
    private String dbServerIp;

    @Value("${dbServer.port}")
    private String dbServerPort;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Resource
    private OrganizationMapper organizationMapper;

    @Override
    public List<ChannelOfSchoolBO> findAllChannels() {
        return organizationMapper.findAllChannels(dbServerIp, dbServerPort,
                dbUserName, dbPassword);
    }
}
