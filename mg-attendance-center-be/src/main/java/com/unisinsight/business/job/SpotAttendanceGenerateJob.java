package com.unisinsight.business.job;

import com.unisinsight.business.bo.OriginalRecordBO;
import com.unisinsight.business.bo.TaskPersonBO;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.mapper.TaskPersonRelationMapper;
import com.unisinsight.business.mapper.TaskResultMapper;
import com.unisinsight.business.model.TaskResultDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生成抽查任务考勤明细
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
@Component
@Slf4j
public class SpotAttendanceGenerateJob implements OriginalRecordConsumer {

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Resource
    private TaskResultMapper taskResultMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void consume(LocalDate date, List<OriginalRecordBO> records) {
        Map<String, OriginalRecordBO> recordMap = new HashMap<>(records.size());
        for (OriginalRecordBO record : records) {
            recordMap.put(record.getPersonNo(), record);
        }

        // 查找出有抽查考勤任务的人员
        List<TaskPersonBO> persons = taskPersonRelationMapper.findHaveTaskPersons(date, recordMap.keySet());
        if (persons.isEmpty()) {
            log.info("未找到在 {} 有抽查任务的人员", date);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<TaskResultDO> results = persons
                .stream()
                .map(person -> {
                    OriginalRecordBO originalRecord = recordMap.get(person.getPersonNo());
                    TaskResultDO result = new TaskResultDO();
                    result.setId(IdGenerateUtil.getId());
                    result.setTaskId(person.getTaskId());
                    result.setResult(AttendanceResult.NORMAL.getType());
                    result.setAttendanceDate(originalRecord.getAttendanceDate());
                    result.setCapturedAt(originalRecord.getPassTime());
                    result.setOriginalRecordId(originalRecord.getId());
                    result.setPersonNo(person.getPersonNo());
                    result.setPersonName(person.getPersonName());
                    result.setOrgIndex(person.getOrgIndex());
                    result.setCreatedAt(now);
                    return result;
                }).collect(Collectors.toList());
        taskResultMapper.batchSave(results);

        Duration duration = Duration.between(now, LocalDateTime.now());
        log.info("保存 {} 条抽查考勤结果，耗时：{} ms <===", results.size(), duration.toMillis());
    }
}
