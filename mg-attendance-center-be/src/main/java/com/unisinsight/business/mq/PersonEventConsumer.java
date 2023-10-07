package com.unisinsight.business.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.common.constants.Actions;
import com.unisinsight.business.mq.event.OMPersonEvent;
import com.unisinsight.business.rpc.dto.OMPersonDTO;
import com.unisinsight.business.service.PersonService;
import com.unisinsight.business.service.TaskPersonRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象管理 人员信息变更事件消费
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/4/12
 * @since 1.0
 */
@Component
@Slf4j
public class PersonEventConsumer {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private PersonService personService;

    @Resource
    private TaskPersonRelationService taskPersonRelationService;

    @KafkaListener(topics = "object-manager-change-topic")
    public void onMessage(ConsumerRecord<String, String> record) {
        OMPersonEvent event;
        try {
            event = objectMapper.readValue(record.value(), OMPersonEvent.class);
        } catch (Exception e) {
            log.warn("不是人员对象事件，不做处理");
            return;
        }

        if (!"person".equals(event.getResourceType())) {
            log.info("不是人员对象事件：{}", event.getResourceType());
            return;
        }

        log.debug("[object-manager-change-topic]： {}", record.value());

        List<OMPersonDTO> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            log.warn("data为空");
            return;
        }

        switch (event.getAction()) {
            case Actions.ADD:
                personService.addSync(data);
                break;
            case Actions.UPDATE:
                personService.updateSync(data);

                List<String> personNos = data.stream()
                        .filter(person -> person.getIsSchool() != null && person.getIsSchool() == 1)
                        .map(OMPersonDTO::getUserCode)
                        .collect(Collectors.toList());
                if (!personNos.isEmpty()) {
                    // 删除抽查任务关联的学生
                    log.info("更新了不在校的学生 {} ", personNos);
                    taskPersonRelationService.deleteByPersonNos(personNos);
                }
                break;
            case Actions.DELETE:
                personNos = data.stream()
                        .map(OMPersonDTO::getUserCode)
                        .collect(Collectors.toList());
                if (personNos.isEmpty()) {
                    log.warn("人员编号为空");
                    return;
                }
                // 删除学生
                personService.deleteByPersonNos(personNos);
                // 删除抽查任务关联的学生
                taskPersonRelationService.deleteByPersonNos(personNos);
                break;
        }
    }
}
