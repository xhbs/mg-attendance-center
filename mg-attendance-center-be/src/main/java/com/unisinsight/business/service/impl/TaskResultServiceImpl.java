package com.unisinsight.business.service.impl;

import com.unisinsight.business.bo.PersonBO;
import com.unisinsight.business.common.enums.AdjustModeEnum;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.mapper.PersonMapper;
import com.unisinsight.business.mapper.ResultChangeRecordMapper;
import com.unisinsight.business.mapper.TaskResultMapper;
import com.unisinsight.business.model.ResultChangeRecordDO;
import com.unisinsight.business.model.TaskResultDO;
import com.unisinsight.business.service.TaskResultService;
import com.unisinsight.framework.common.util.user.User;
import com.unisinsight.framework.common.util.user.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 抽查考勤结果服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/6
 */
@Service
@Slf4j
public class TaskResultServiceImpl implements TaskResultService {

    @Resource
    private TaskResultMapper taskResultMapper;

    @Resource
    private PersonMapper personMapper;

    @Resource
    private ResultChangeRecordMapper resultChangeRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateResults(String personNo, LocalDate startDate, LocalDate endDate, AttendanceResult result,
                              String comment, AdjustModeEnum mode) {
        // 查询待更新记录
        Example example = Example.builder(TaskResultDO.class)
                .where(Sqls.custom()
                        .andEqualTo("personNo", personNo)
                        .andGreaterThanOrEqualTo("attendanceDate", startDate)
                        .andLessThanOrEqualTo("attendanceDate", endDate)
                )
                .build();
        List<TaskResultDO> records = taskResultMapper.selectByCondition(example);
        log.info("[调整抽查考勤] - 找到 {} 在 {} - {} 的 {} 条记录", personNo, startDate, endDate, records.size());

        if (records.isEmpty()) {
            // 生成考勤记录
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        User user = UserHandler.getUser();

        List<ResultChangeRecordDO> changeRecords = new ArrayList<>(records.size());
        for (TaskResultDO record : records) {
            ResultChangeRecordDO changeRecord = new ResultChangeRecordDO();
            changeRecord.setAttendanceResultId(record.getId());
            changeRecord.setResultBeforeChange(record.getResult());
            changeRecord.setResultAfterChange(result.getType());
            changeRecord.setComment(comment);
            changeRecord.setMode(mode.getValue());
            changeRecord.setChangedAt(now);
            changeRecord.setChangedBy(user == null ? "" : user.getUserCode());
            changeRecords.add(changeRecord);

            // 更新考勤结果
            record.setResult(result.getType());
            record.setUpdatedAt(LocalDateTime.now());
            taskResultMapper.updateByPrimaryKeySelective(record);
        }

        // 保存变更记录
        resultChangeRecordMapper.insertList(changeRecords);

        log.info("[调整抽查考勤] - 更新了 {} 的 {} 条记录", personNo, records.size());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrCreateResult(String personNo, Integer taskId, LocalDate date, AttendanceResult result,
                                     String comment, AdjustModeEnum mode) {
        // 查询待更新记录
        Example example = Example.builder(TaskResultDO.class)
                .where(Sqls.custom()
                        .andEqualTo("taskId", taskId)
                        .andEqualTo("personNo", personNo)
                        .andEqualTo("attendanceDate", date)
                )
                .build();
        List<TaskResultDO> records = taskResultMapper.selectByCondition(example);

        LocalDateTime now = LocalDateTime.now();
        User user = UserHandler.getUser();

        if (records.isEmpty()) {
            log.info("{} 在 {} 不存在考勤记录，新生成", personNo, date);

            // 当天不存在考勤记录，新生成
            PersonBO person = personMapper.findPersonBO(personNo);
            TaskResultDO record = new TaskResultDO();
            record.setId(IdGenerateUtil.getId());
            record.setTaskId(taskId);
            record.setResult(result.getType());
            record.setAttendanceDate(date);
            record.setPersonNo(person.getPersonNo());
            record.setPersonName(person.getPersonName());
            record.setOrgIndex(person.getOrgIndex());
            record.setCreatedAt(now);

            taskResultMapper.batchSave(Collections.singletonList(record));
        } else {
            // 当天存在考勤记录，更新
            TaskResultDO record = records.get(0);
            int beforeResult = record.getResult();
            log.info("{} 在 {} 存在考勤记录，更新", personNo, record.getId());

            record.setUpdatedAt(now);
            record.setResult(result.getType());
            taskResultMapper.updateByPrimaryKeySelective(record);

            // 保存变更记录
            ResultChangeRecordDO changeRecord = new ResultChangeRecordDO();
            changeRecord.setAttendanceResultId(record.getId());
            changeRecord.setResultBeforeChange(beforeResult);
            changeRecord.setResultAfterChange(result.getType());
            changeRecord.setComment(comment);
            changeRecord.setMode(mode.getValue());
            changeRecord.setChangedAt(now);
            changeRecord.setChangedBy(user == null ? "" : user.getUserCode());
            resultChangeRecordMapper.insertUseGeneratedKeys(changeRecord);

            log.info("[调整抽查考勤] - 更新了 {} 在 {} 的考勤记录", personNo, date);
        }
    }
}
