package com.unisinsight.business.job;

import com.google.common.collect.Lists;
import com.unisinsight.business.bo.TaskPersonBO;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.TaskResultDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 生成抽查任务考勤明细
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class SpotAttendanceAbsenceJob implements AttendanceAbsenceJob {

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Resource
    private TaskResultMapper taskResultMapper;

    @Resource
    private LeaveRecordMapper leaveRecordMapper;

    @Resource
    private PracticePersonMapper practicePersonMapper;

    @Resource
    private OriginalRecordMapper originalRecordMapper;

    @Scheduled(cron = "${cron.SpotAttendanceAbsenceJob}")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run() {
        log.info("[定时任务] - 生成抽查考勤缺勤结果 ===>");

        // 生成昨天的
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generate(yesterday);
    }

    /**
     * 生成某一天的缺勤结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generate(LocalDate date) {
        // 查找出当天有考勤任务但缺勤的人员
        List<TaskPersonBO> absencePersons = taskPersonRelationMapper.findAbsencePersonsAtDate(date);
        if (absencePersons.isEmpty()) {
            log.info("没有在 {} 有考勤任务并且缺勤的人员", date);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Lists.partition(absencePersons, 1000)// 每次为1000人生成结果
                .forEach(persons -> {
                    List<String> personNos = persons
                            .stream()
                            .map(TaskPersonBO::getPersonNo)
                            .collect(Collectors.toList());

                    // 查询正在请假的学生
                    Set<String> leavingPersons = leaveRecordMapper.findLeavingPersonsAtDate(personNos, date);
                    // 查询正在实习的学生
                    Set<String> practicePersons = practicePersonMapper.findPracticePersons(personNos, date);
                    // 查询当天有打卡，但是未生成正常结果的学生
                    Set<String> normalPersons = originalRecordMapper.filterPersonNos(
                            LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX), personNos);
                    log.info("有 {} 个学生请假中，{} 个学生实习中，{} 个学生有打卡未生成正常结果",
                            leavingPersons.size(), practicePersons.size(), normalPersons.size());

                    List<TaskResultDO> results = persons
                            .stream()
                            .map(person -> {
                                TaskResultDO record = new TaskResultDO();
                                record.setId(IdGenerateUtil.getId());

                                if (leavingPersons.contains(person.getPersonNo())) {
                                    record.setResult(AttendanceResult.LEAVE.getType());
                                } else if (practicePersons.contains(person.getPersonNo())) {
                                    record.setResult(AttendanceResult.PRACTICE.getType());
                                } else if (normalPersons.contains(person.getPersonNo())) {
                                    record.setResult(AttendanceResult.NORMAL.getType());
                                } else {
                                    record.setResult(AttendanceResult.ABSENCE.getType());
                                }

                                record.setAttendanceDate(date);
                                record.setTaskId(person.getTaskId());
                                record.setPersonNo(person.getPersonNo());
                                record.setPersonName(person.getPersonName());
                                record.setOrgIndex(person.getOrgIndex());
                                record.setCreatedAt(now);
                                return record;
                            }).collect(Collectors.toList());
                    taskResultMapper.batchSave(results);
                });

        Duration duration = Duration.between(now, LocalDateTime.now());
        log.info("生成 {} 条缺勤抽查考勤结果，耗时：{} ms <===", absencePersons.size(), duration.toMillis());
    }
}
