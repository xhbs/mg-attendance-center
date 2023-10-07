package com.unisinsight.business.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.mq.event.OMPersonGroupEvent;
import com.unisinsight.business.rpc.dto.OMGroupDTO;
import com.unisinsight.business.service.DispositionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象管理 人员分组变更事件消费
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/4/12
 * @since 1.0
 */
@Component
@Slf4j
public class PersonGroupEventConsumer {

    @Resource
    private DispositionService dispositionService;

    @Resource
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "object-manager-lib-change-topic")
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("[object-manager-lib-change-topic]：{}", record.value());

        OMPersonGroupEvent event;
        try {
            event = objectMapper.readValue(record.value(), OMPersonGroupEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("事件数据解码失败", e);
            return;
        }

        if ("lib".equals(event.getResourceType())) {
            handleGroupEvent(event);
        }
    }

    /**
     * 处理分组事件
     */
    private void handleGroupEvent(OMPersonGroupEvent event) {
        List<OMGroupDTO> groups = event.getData().toJavaList(OMGroupDTO.class);
        // 名单库ID列表
        List<String> tabIds = groups.stream()
                .filter(omGroupDTO -> StringUtils.isNotEmpty(omGroupDTO.getTabId()))
                .map(OMGroupDTO::getTabId)
                .collect(Collectors.toList());

        switch (event.getAction()) {
            case "add":// 创建分组
            case "update":// 更新分组
                if (!tabIds.isEmpty()) {
                    // 自动创建考勤布控
                    dispositionService.createDispositions(tabIds);
                }
                break;
            case "delete":
                // 删除分组
                if (!tabIds.isEmpty()) {
                    // 删除考勤布控
                    dispositionService.deleteDispositions(tabIds);
                }
                break;
        }
    }
}
