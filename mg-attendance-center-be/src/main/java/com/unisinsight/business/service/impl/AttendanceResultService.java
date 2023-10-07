package com.unisinsight.business.service.impl;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.CaptureRecordDTO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.rpc.UDMClient;
import com.unisinsight.business.rpc.dto.UDMChannelDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日常考勤和抽查考勤明细公共逻辑
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
@Slf4j
class AttendanceResultService {

    @Resource
    private OriginalRecordMapper originalRecordMapper;

    @Resource
    private UDMClient udmClient;

    @Resource
    protected PersonMapper personMapper;

    @Resource
    protected ResultChangeRecordMapper resultChangeRecordMapper;

    @Resource
    protected LeaveRecordMapper leaveRecordMapper;

    @Resource
    protected PracticeRecordMapper practiceRecordMapper;

    /**
     * 查找最近三次抓拍记录
     */
    List<CaptureRecordDTO> getRecentCaptureRecords(String personNO, LocalDateTime timeLimit) {
        List<CaptureRecordDTO> captureRecords = originalRecordMapper.findRecentCaptureRecords(personNO,
                timeLimit, 3);
        if (captureRecords.isEmpty()) {
            return captureRecords;
        }

        // 从udm去查通道信息
        PaginationRes<UDMChannelDTO> res;
        try {
            res = udmClient.getChannelList(captureRecords.stream()
                    .map(CaptureRecordDTO::getChannelId)
                    .collect(Collectors.joining(",")));
        } catch (Exception e) {// 查询失败不影响查询考勤结果
            log.error("查询通道信息失败", e);
            return captureRecords;
        }
        if (CollectionUtils.isNotEmpty(res.getData())) {
            Map<String, String> channelNameMap = res.getData()
                    .stream()
                    .collect(Collectors.toMap(UDMChannelDTO::getApeId, UDMChannelDTO::getOwnerApsName));
            for (CaptureRecordDTO captureRecord : captureRecords) {
                captureRecord.setDeviceName(channelNameMap.get(captureRecord.getChannelId()));
            }
        }
        return captureRecords;
    }
}
