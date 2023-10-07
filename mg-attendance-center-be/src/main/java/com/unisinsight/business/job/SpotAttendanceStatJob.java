package com.unisinsight.business.job;

import com.unisinsight.business.bo.TaskResultCountBO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.model.SpotCheckTaskDO;
import com.unisinsight.business.model.TaskResultStatDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生成抽查考勤统计结果
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
@Component
@Slf4j
public class SpotAttendanceStatJob implements AttendanceAbsenceJob {

    @Resource
    private SpotCheckTaskMapper spotCheckTaskMapper;

    @Resource
    private TaskResultMapper taskResultMapper;

    @Resource
    private TaskOrgRelationMapper taskOrgRelationMapper;

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Resource
    private TaskResultStatMapper taskResultStatMapper;

    @Scheduled(cron = "${cron.SpotAttendanceStatJob}")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run() {
        log.info("[定时任务] - 生成抽查考勤统计 ===>");

        // 生成昨天的
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generate(yesterday);
    }

    /**
     * 生成某一天的数据
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generate(LocalDate date) {
        // 查询当天有抽查日期的抽查任务
        List<SpotCheckTaskDO> tasks = spotCheckTaskMapper.findByDate(date);
        log.info("查询到 {} 有 {} 个抽查任务", date, tasks);

        if (CollectionUtils.isNotEmpty(tasks)) {
            for (SpotCheckTaskDO task : tasks) {
                generate(date, task);
            }
        }
    }

    /**
     * 为抽查任务生成某一天的统计数据
     */
    private void generate(LocalDate date, SpotCheckTaskDO task) {
        // 查询任务下面关联的所有学校
        List<OrganizationDO> schools = taskOrgRelationMapper.findSchoolsOfTask(task.getId());
        log.info("查询到任务 {} 下关联的 {} 个学校", task.getId(), schools.size());

        if (CollectionUtils.isNotEmpty(schools)) {
            List<TaskResultStatDO> records = schools
                    .stream()
                    .map(school -> generate(date, task, school))
                    .collect(Collectors.toList());
            taskResultStatMapper.batchSave(records);
            log.info("生成任务 {} 在 {} 的 {} 条统计记录", task.getId(), date, records.size());
        }
    }

    /**
     * 为抽查任务生成单个学校某一天的统计数据
     */
    private TaskResultStatDO generate(LocalDate date, SpotCheckTaskDO task, OrganizationDO school) {
        // 查询学校下被抽查的人数
        List<String> personNos = taskPersonRelationMapper.findPersonsOfSchool(school.getOrgIndex());
        int personCount = personNos.size();
        log.info("查询到任务 {} 下关联学校 {} 下 {} 个抽查学生", task.getId(), school.getOrgIndex(), personCount);

        // 统计在校学生
        TaskResultCountBO count;
        int inSchoolNum;
        int absenceNum;
        double absenceRate;
        if (personCount == 0) {
            count = new TaskResultCountBO();
            inSchoolNum = 0;
            absenceNum = 0;
            absenceRate = 0;
        } else {
            count = taskResultMapper.countAtDate(task.getId(), personNos, date);
            // 在校人数 = 正常人数 + 请假人数 + 实习人数 + 申诉通过人数
            inSchoolNum = count.getNumOfNormal() + count.getNumOfLeave() + count.getNumOfPractice()
                    + count.getNumOfAppeal();
            absenceNum = personCount - inSchoolNum;
            absenceRate = new BigDecimal(absenceNum * 100.0f / personCount).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        TaskResultStatDO record = new TaskResultStatDO();
        record.setTaskId(task.getId());
        record.setAttendanceDate(date);
        record.setSchoolOrgIndex(school.getOrgIndex());
        record.setSchoolOrgName(school.getOrgName());
        record.setIndexPath(school.getIndexPath());
        record.setIndexPathName(school.getIndexPathName());
        record.setStudentNum(personCount);
        record.setNormalNum(inSchoolNum);
        record.setAbsenceNum(absenceNum);
        record.setAbsenceRate(absenceRate);
        record.setLeaveNum(count.getNumOfLeave());
        record.setPracticeNum(count.getNumOfPractice());
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }
}
