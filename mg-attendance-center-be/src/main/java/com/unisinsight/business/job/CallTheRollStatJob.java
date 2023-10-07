package com.unisinsight.business.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.unisinsight.business.common.constants.Constants;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.enums.SpotCheckTaskState;
import com.unisinsight.business.common.utils.UserCache;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.request.SpotCheckTaskAddReqDTO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.*;
import com.unisinsight.business.service.SpotCheckTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 点名统计定时任务
 */
@Component
@Slf4j
public class CallTheRollStatJob {

    @Resource
    private SpotCheckTaskMapper spotCheckTaskMapper;
    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;
    @Resource
    private TaskOrgRelationMapper taskOrgRelationMapper;
    @Resource
    private TaskResultMapper taskResultMapper;
    @Resource
    private CallTheRollStatDao callTheRollStatDao;

    /**
     * 从17号开始,每天查询有没有完成2次点名的任务,有的话则统计
     */
    @Scheduled(cron = "0 0 1 17/1 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void statistics() {
        log.info("[定时任务] - 生成点名考勤统计 ===>");

        // 生成昨天的
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generate(yesterday);
    }

    private void generate(LocalDate yesterday) {
        List<SpotCheckTaskDO> tasks = spotCheckTaskMapper.findCalltheRollByDate(yesterday);
        log.info("查询到 {} 有 {} 个下半月点名任务", yesterday, tasks);

        if (CollectionUtils.isNotEmpty(tasks)) {
            for (SpotCheckTaskDO task : tasks) {
                SpotCheckTaskDO firstTask = firstHalfOfTheMonth(task);
                generate(firstTask, task);
            }
        }
    }

    private SpotCheckTaskDO firstHalfOfTheMonth(SpotCheckTaskDO task) {
        log.info("查询到{}的上半月点名", task.getName());
        return spotCheckTaskMapper.selectByPrimaryKey(task.getCallTheRollFirstTaskId());
    }

    /**
     * 为抽查任务生成某一天的统计数据
     */
    private void generate(SpotCheckTaskDO firstTask, SpotCheckTaskDO secondTask) {
        // 查询任务下面关联的所有学校
        List<OrganizationDO> schools = taskOrgRelationMapper.findSchoolsOfTask(firstTask.getId());
        log.info("查询到{}月点名 下关联的 {} 个学校", firstTask.getId(), schools.size());

        if (CollectionUtils.isNotEmpty(schools)) {
            List<CallTheRollStat> records = schools
                    .stream()
                    .map(school -> generate(firstTask, secondTask, school))
                    .collect(Collectors.toList());
            callTheRollStatDao.insertBatch(records);
        }
    }

    private CallTheRollStat generate(SpotCheckTaskDO firstTask, SpotCheckTaskDO secondTask, OrganizationDO school) {
        // 查询学校下被抽查的人数
        List<String> personNos = taskPersonRelationMapper.findPersonsOfSchool(school.getOrgIndex());
        int personCount = personNos.size();
        log.info("查询到{}月点名的关联学校 {} 下 {} 个抽查学生", firstTask.getMonth(), school.getOrgIndex(), personCount);
        List<TaskResultDO> firstResults = taskResultMapper.selectByTaskIdAndOrg(firstTask.getId(), school.getOrgIndex());
        List<TaskResultDO> secondResults = taskResultMapper.selectByTaskIdAndOrg(secondTask.getId(), school.getOrgIndex());

        Map<String, Integer> firstResultsMap = firstResults.stream().collect(Collectors.toMap(TaskResultDO::getPersonNo, TaskResultDO::getResult));
        Map<String, Integer> secondResultsMap = secondResults.stream().collect(Collectors.toMap(TaskResultDO::getPersonNo, TaskResultDO::getResult));

        int normalSum = 0;
        int absenceSum = 0;
        int leaveSum = 0;
        int practiceSum = 0;
        int applySum = 0;

        for (String personNo : personNos) {
            Integer result = result(firstResultsMap.get(personNo), secondResultsMap.get(personNo));
            if (result.compareTo(AttendanceResult.NORMAL.getType()) == 0) {
                normalSum += 1;
            }
            if (result.compareTo(AttendanceResult.LEAVE.getType()) == 0) {
                leaveSum += 1;
            }
            if (result.compareTo(AttendanceResult.PRACTICE.getType()) == 0) {
                practiceSum += 1;
            }
            if (result.compareTo(AttendanceResult.ABSENCE.getType()) == 0) {
                absenceSum += 1;
            }
            if (result.compareTo(AttendanceResult.APPEAL.getType()) == 0) {
                applySum += 1;
            }
        }
        CallTheRollStat stat = new CallTheRollStat();
        stat.setTaskCreateBy(firstTask.getCreatorCode());
        stat.setMonth(firstTask.getMonth());
        stat.setSchoolOrgName(school.getOrgName());
        stat.setSchoolOrgIndex(school.getOrgIndex());
        stat.setCreateTime(LocalDate.now());
        stat.setAbsenceNum(absenceSum);
        stat.setNormalNum(normalSum + leaveSum + practiceSum + applySum);
        stat.setPracticeNum(practiceSum);
        stat.setLeaveNum(leaveSum);
        stat.setStudentNum(stat.getAbsenceNum() + stat.getNormalNum());
        return stat;
    }

    public static Integer result(Integer one, Integer two) {
        if (one == null) {
            return two;
        }
        if (two == null) {
            return one;
        }
        if (one.compareTo(two) == 0) {
            return one;
        }

        for (AttendanceResult result : AttendanceResult.values()) {
            if (one.compareTo(result.getType()) == 0 || two.compareTo(result.getType()) == 0) {
                return result.getType();
            }
        }
        return AttendanceResult.ABSENCE.getType();
    }
}

