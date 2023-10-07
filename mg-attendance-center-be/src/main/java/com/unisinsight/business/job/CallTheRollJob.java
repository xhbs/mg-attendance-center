package com.unisinsight.business.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.unisinsight.business.common.constants.Constants;
import com.unisinsight.business.common.enums.SpotCheckTaskState;
import com.unisinsight.business.common.utils.UserCache;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.request.SpotCheckTaskAddReqDTO;
import com.unisinsight.business.mapper.SpotCheckTaskMapper;
import com.unisinsight.business.mapper.TaskOrgRelationMapper;
import com.unisinsight.business.model.SpotCheckTaskDO;
import com.unisinsight.business.model.TaskOrgRelationDO;
import com.unisinsight.business.service.SpotCheckTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 点名定时任务
 */
@Component
@Slf4j
public class CallTheRollJob {

    @Resource
    private SpotCheckTaskMapper spotCheckTaskMapper;
    @Resource
    private SpotCheckTaskService spotCheckTaskService;
    @Resource
    private TaskOrgRelationMapper taskOrgRelationMapper;

    /**
     * 下半月点名,下半月每天都执行,重复创建会报错
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 1 16/1 * ?")
    public void secondOfMonth() {
        Example build = Example.builder(SpotCheckTaskDO.class)
                .where(Sqls.custom()
                        .andLessThan("endDate", LocalDate.now())
                        .andEqualTo("callTheRoll", Constants.TRUE)
                        .andEqualTo("state", SpotCheckTaskState.FINISHED.getValue())
                        .andEqualTo("month", DateUtil.format(new Date(), "yyyy-MM"))).build();
        List<SpotCheckTaskDO> tasks = spotCheckTaskMapper.selectByCondition(build);
        //过滤出没有下半月任务的
        List<SpotCheckTaskDO> spotCheckTaskDOS = selectSecondTask(tasks);
        if (CollUtil.isEmpty(spotCheckTaskDOS)) {
            log.info("今天没有要新建的下半月点名任务!");
            return;
        }

        UserCache.getUserStruct(UserUtils.getUserAuto());

        for (SpotCheckTaskDO task : spotCheckTaskDOS) {
            try {
                spotCheckTaskService.addCallTheRoll(generate(task));
            } catch (Exception e) {
                log.info("任务:{} 创建下半月点名报错", task.getName(), e);
            }
        }
        UserCache.clear();
    }

    private List<SpotCheckTaskDO> selectSecondTask(List<SpotCheckTaskDO> tasks) {
        List<Integer> ids = tasks.stream().map(SpotCheckTaskDO::getId).collect(Collectors.toList());
        Example build = Example.builder(SpotCheckTaskDO.class)
                .where(Sqls.custom()
                        .andIn("callTheRollFirstTaskId", ids)).build();
        List<SpotCheckTaskDO> spotCheckTaskDOS = spotCheckTaskMapper.selectByCondition(build);
        if (CollUtil.isEmpty(spotCheckTaskDOS)) {
            return tasks;
        }
        return spotCheckTaskDOS.stream().filter(v -> !ids.contains(v.getCallTheRollFirstTaskId())).collect(Collectors.toList());

    }

    private List<String> getOrgList(int taskId) {
        List<TaskOrgRelationDO> orgs = taskOrgRelationMapper.selectByCondition(Example.builder(TaskOrgRelationDO.class)
                .where(Sqls.custom().andEqualTo("taskId", taskId))
                .build());
        return orgs.stream().map(TaskOrgRelationDO::getOrgIndex).collect(Collectors.toList());
    }

    private SpotCheckTaskAddReqDTO generate(SpotCheckTaskDO taskDO) {
        SpotCheckTaskAddReqDTO req = new SpotCheckTaskAddReqDTO();

        BeanUtils.copyProperties(taskDO, req);
        req.setAuto(Boolean.TRUE);
        req.setStartDate(LocalDate.now());
        req.setEndDate(req.getStartDate().with(TemporalAdjusters.lastDayOfMonth()));
        req.setTargetOrgIndexes(getOrgList(taskDO.getId()));
        req.setCallTheRollFirstTaskId(taskDO.getId());
        return req;
    }
}

