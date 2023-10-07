package com.unisinsight.business.mq;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.common.constants.Actions;
import com.unisinsight.business.common.enums.OrgType;
import com.unisinsight.business.common.enums.ResTreeType;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.mq.event.UUVEvent;
import com.unisinsight.business.service.AttendanceEventFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 公共服务Kafka消息推送
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/24
 * @since 1.0
 */
@Component
@Slf4j
public class UUVKafkaConsumer {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Resource
    private OrganizationMapper organizationMapper;

    @Resource
    private AttendanceEventFilter attendanceEventFilter;

    /**
     * 组织更新消息 虚拟组织字段
     * {"action":"add","data":[{"deleted":0,"displayOrder":900.0,"indexPath":"0/1/2/3/","orgId":16,"orgIndex":"18",
     * "orgName":"六华区","orgParentIndex":"3","parentId":4,"subType":"3","type":"1"}],"origin":"UUV","type":"1"}
     */
    @KafkaListener(topics = {"organization-change-topic"})
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("[organization-change-topic]: {}", record.value());

        UUVEvent event;
        try {
            event = objectMapper.readValue(record.value(), UUVEvent.class);
        } catch (Exception e) {
            log.error("UUV 消息解码失败", e);
            return;
        }

        log.info("[组织树推送] - action: {}, type:{}", event.getAction(), event.getType());

        if (CollectionUtils.isEmpty(event.getData())) {
            log.warn("变更数据为空");
            return;
        }

        if (event.getType().equals(String.valueOf(ResTreeType.DEVICE.getValue()))) {
            log.info("设备组织变更");
            // 刷新缓存
            log.info("设备组织变更，刷新缓存");
            attendanceEventFilter.refreshChannelCache();
        } else if (event.getType().equals(String.valueOf(ResTreeType.PERSON.getValue()))) {
            log.info("人员组织变更");
            onPersonOrgChange(event);
        }
    }

    /**
     * 处理人员组织树变更
     */
    private void onPersonOrgChange(UUVEvent event) {
        List<UUVEvent.Data> data = event.getData();

        // 变更的是否有学校组织
        boolean hasSchoolOrg = false;
        List<OrganizationDO> orgList = new ArrayList<>(data.size());
        List<String> orgIndexes = new ArrayList<>(data.size());

        for (UUVEvent.Data src : data) {
            if (src.getSubType() == OrgType.SCHOOL.getValue()) {
                hasSchoolOrg = true;
            }
            orgList.add(OrganizationDO.builder()
                    .id(src.getOrgId())
                    .orgIndex(src.getOrgIndex())
                    .orgName(src.getOrgName())
                    .orgParentIndex(src.getOrgParentIndex())
                    .subType(src.getSubType())
                    .indexPath(src.getIndexPath())
                    .isVirtual(src.getIsVirtual() == 1)
                    .build());
            orgIndexes.add(src.getOrgIndex());
        }

        switch (event.getAction()) {
            case Actions.ADD:
                // 添加组织
                organizationMapper.batchSave(orgList);
                log.info("添加了 {} 个组织", data.size());
                break;
            case Actions.UPDATE:
                // 更新组织
                organizationMapper.batchUpdate(orgList);
                log.info("更新了 {} 个组织", data.size());
                break;
            case Actions.DELETE:
                // 删除组织
                Example example = Example.builder(OrganizationDO.class)
                        .where(Sqls.custom()
                                .andEqualTo("orgIndex", orgIndexes))
                        .build();
                organizationMapper.deleteByCondition(example);
                log.info("删除了 {} 个组织", orgIndexes.size());
                break;
        }

        if (hasSchoolOrg) {
            // 刷新缓存
            log.info("有学校组织变更，刷新缓存");
            attendanceEventFilter.refreshChannelCache();
        }
    }
}
