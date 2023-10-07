package com.unisinsight.business.mq;

import com.alibaba.fastjson.JSON;
import com.unisinsight.business.common.constants.Actions;
import com.unisinsight.business.rpc.dto.ChannelDTO;
import com.unisinsight.business.rpc.dto.DeviceChangeData;
import com.unisinsight.business.rpc.dto.DeviceChangeEventDTO;
import com.unisinsight.business.rpc.dto.DeviceDTO;
import com.unisinsight.business.service.AttendanceEventFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 接收设备数据变更
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
@Component
@Slf4j
public class UDMKafkaConsumer {

    @Resource
    private AttendanceEventFilter attendanceEventFilter;

    /**
     * 接收设备数据变更
     */
    @KafkaListener(topics = {"uniform-device-change"})
    public void onDeviceChanged(ConsumerRecord<String, String> consumerRecord) {
        log.info("[uniform-device-change]: {}", consumerRecord.value());

        DeviceChangeEventDTO event = JSON.parseObject(consumerRecord.value(), DeviceChangeEventDTO.class);
        List<DeviceChangeData> data = event.getData();
        if (data == null || data.isEmpty()) {
            log.warn("推送数据为空");
            return;
        }

        List<String> channelIds = new ArrayList<>();
        for (DeviceChangeData item : data) {
            List<DeviceDTO> deviceList = item.getDeviceList();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                for (DeviceDTO device : deviceList) {
                    List<ChannelDTO> channelList = device.getChannelList();
                    if (CollectionUtils.isNotEmpty(channelList)) {
                        for (ChannelDTO channel : channelList) {
                            channelIds.add(channel.getApeId());
                        }
                    }
                }
            }
            List<ChannelDTO> channelList = item.getChannelList();
            if (CollectionUtils.isNotEmpty(channelList)) {
                for (ChannelDTO channel : channelList) {
                    channelIds.add(channel.getApeId());
                }
            }
        }

        log.info("{} 个通道变更", channelIds.size());

        // 刷新缓存s
        if (event.getAction().equals(Actions.ADD) && !channelIds.isEmpty()) {
            log.info("添加通道，刷新缓存");
            attendanceEventFilter.refreshChannelCache();
        }
    }
}
