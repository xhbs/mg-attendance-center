package com.unisinsight.business.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.common.enums.OriginalRecordSource;
import com.unisinsight.business.common.enums.OriginalRecordStatus;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.model.OriginalRecordDO;
import com.unisinsight.business.mq.event.EgsEvent;
import com.unisinsight.business.service.AttendanceEventFilter;
import com.unisinsight.business.service.OriginalRecordService;
import com.unisinsight.framework.common.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 门禁事件消费
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/8
 * @since 1.0
 */
@Component
@Slf4j
public class EgsEventConsumer {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private OriginalRecordService originalRecordService;

    @Resource
    private AttendanceEventFilter attendanceEventFilter;

    @KafkaListener(topics = "uss-egs-event", containerFactory = "egsBatchFactory")
    public void onMessage(ConsumerRecords<String, String> records) {
        int count = records.count();
        if (count <= 0) {
            return;
        }
        log.info("[门禁事件] - 收到 {} 条消息", count);

        List<OriginalRecordDO> originalRecords = new ArrayList<>(count);
        for (ConsumerRecord<String, String> record : records) {
            try {
                EgsEvent event = objectMapper.readValue(record.value(), EgsEvent.class);
                if (event.getPersonCode() == null) {
                    log.warn("personCode 为空: {}", record.value());
                    continue;
                }

                //  过滤非考勤人员、打卡频率限制
                if (attendanceEventFilter.checkAttendanceEvent(event.getPersonCode(), event.getPassagewayCode(),
                        event.getUploadTime())) {
                    originalRecords.add(genOriginData(event));
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        // 保存原始数据
        if (!originalRecords.isEmpty()) {
            originalRecordService.batchSave(originalRecords);
        }
    }

    /**
     * 根据门禁事件生成原始数据
     */
    private OriginalRecordDO genOriginData(EgsEvent event) {
        OriginalRecordDO data = new OriginalRecordDO();
        data.setId(IdGenerateUtil.getId());
        data.setPersonNo(event.getPersonCode());
        data.setCaptureImage(event.getCardUserImgUrl());
        data.setDeviceCode(event.getPassagewayCode());
        data.setPassTime(DateUtils.fromMilliseconds(event.getUploadTime()));
        data.setSource(OriginalRecordSource.DISPOSITION.getValue());
        data.setCreateTime(LocalDateTime.now());
        return data;
    }
}
