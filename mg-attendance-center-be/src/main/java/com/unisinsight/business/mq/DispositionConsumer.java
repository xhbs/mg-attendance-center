package com.unisinsight.business.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.common.enums.AttendanceEventType;
import com.unisinsight.business.common.enums.OriginalRecordSource;
import com.unisinsight.business.common.enums.OriginalRecordStatus;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.model.OriginalRecordDO;
import com.unisinsight.business.mq.event.DispositionEvent;
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
 * 布控告警消费线程
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/8
 * @since 1.0
 */
//@Component
@Slf4j
public class DispositionConsumer {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private OriginalRecordService originalRecordService;

    @Resource
    private AttendanceEventFilter attendanceEventFilter;

    @KafkaListener(topics = "disposition_topic", containerFactory = "dispositionBatchFactory")
    public void onMessage(ConsumerRecords<String, String> records) {
        int count = records.count();
        if (count <= 0) {
            return;
        }
        log.info("[布控告警] - 收到 {} 条消息", count);

        List<OriginalRecordDO> originalRecords = new ArrayList<>(count);
        for (ConsumerRecord<String, String> record : records) {
            try {
                DispositionEvent event = objectMapper.readValue(record.value(), DispositionEvent.class);
                String personNo = event.getIdNumber();
                if (personNo == null) {
                    log.info("人脸 {} 没有idNumber", event.getFaceId());
                    continue;
                }

                //  过滤非考勤人员、打卡频率限制
                if (attendanceEventFilter.checkAttendanceEvent(personNo, event.getDeviceId(), event.getPassTime())) {
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
     * 根据布控告警生成原始数据
     */
    private OriginalRecordDO genOriginData(DispositionEvent event) {
        OriginalRecordDO data = new OriginalRecordDO();
        data.setId(IdGenerateUtil.getId());
        data.setPersonNo(event.getIdNumber());// 云南中职这里直接取的是person_no
        data.setCaptureImage(event.getImageUrlPart());
        data.setDeviceCode(event.getDeviceId());
        data.setPassTime(DateUtils.fromMilliseconds(event.getPassTime()));
        data.setSource(OriginalRecordSource.DISPOSITION.getValue());
        data.setCreateTime(LocalDateTime.now());
        return data;
    }
}
