package com.unisinsight.business.service.impl;

import com.unisinsight.business.mapper.TaskPersonRelationMapper;
import com.unisinsight.business.model.TaskPersonRelationDO;
import com.unisinsight.business.service.TaskPersonRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * 抽查任务与考勤人员关联服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/2
 */
@Slf4j
@Service
public class TaskPersonRelationServiceImpl implements TaskPersonRelationService {

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Async
    @Override
    public void saveTaskPersons(Integer taskId, List<String> classOrgIndexes, LocalDate startDate, LocalDate endDate,
                                Integer absenceRate) {
        // 查出符合条件的人员并关联
        taskPersonRelationMapper.saveTaskPersons(taskId, classOrgIndexes, startDate, endDate, absenceRate);
        log.info("保存任务 {} 关联的人员成功", taskId);
    }

    /**
     * 保存班级组织下的所有学生
     *
     * @param taskId
     * @param classOrgIndexes
     */
    @Async(value = "taskExecutor")
    @Override
    public void saveTaskPersonsCtr(Integer taskId, List<String> classOrgIndexes, Integer callTheRollFirstTaskId) {
        // 查出符合条件的人员并关联
        taskPersonRelationMapper.saveTaskPersonsCtr(taskId, classOrgIndexes, callTheRollFirstTaskId);
        log.info("保存点名任务 {} 关联的人员成功", taskId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByTaskIds(List<Integer> taskIds) {
        Example example = Example.builder(TaskPersonRelationDO.class)
                .where(Sqls.custom()
                        .andIn("taskId", taskIds)
                )
                .build();
        int cnt = taskPersonRelationMapper.deleteByCondition(example);
        log.info("删除了抽查任务 {} 关联的 {} 个人员", taskIds, cnt);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByPersonNos(List<String> personNos) {
        Example example = Example.builder(TaskPersonRelationDO.class)
                .where(Sqls.custom()
                        .andIn("personNo", personNos)
                )
                .build();
        int cnt = taskPersonRelationMapper.deleteByCondition(example);
        log.info("删除了人员：{}，关联的 {} 条记录", personNos, cnt);
    }
}
