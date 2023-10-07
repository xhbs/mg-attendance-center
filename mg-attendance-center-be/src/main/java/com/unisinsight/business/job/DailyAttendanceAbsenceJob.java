package com.unisinsight.business.job;

import com.google.common.collect.Lists;
import com.unisinsight.business.bo.PersonBO;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.enums.ExcludeDateType;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.DailyAttendanceResultDO;
import com.unisinsight.business.model.DailyAttendanceSettingDO;
import com.unisinsight.business.service.DailyAttendanceResultService;
import com.unisinsight.business.service.DailyAttendanceSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 日常考勤缺勤结果生成定时任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class DailyAttendanceAbsenceJob implements AttendanceAbsenceJob {

    @Resource
    private DailyAttendanceResultService dailyAttendanceResultService;

    @Resource
    private DailyAttendanceSettingService attendanceSettingService;

    @Resource
    private DailyAttendanceResultMapper dailyAttendanceResultMapper;

    @Resource
    private DailyAttendanceExcludeDateMapper dailyAttendanceExcludeDateMapper;

    @Resource
    private LeaveRecordMapper leaveRecordMapper;

    @Resource
    private PracticePersonMapper practicePersonMapper;

    @Resource
    private OriginalRecordMapper originalRecordMapper;

    @Scheduled(cron = "${cron.DailyAttendanceAbsenceJob}")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run() {
        log.info("[定时任务] - 生成日常考勤缺勤结果 ===>");

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
        DailyAttendanceSettingDO setting = attendanceSettingService.getSetting();
        if (setting == null) {
            log.warn("未找到日常考勤设置");
            return;
        }

        if (!setting.getEnable()) {
            log.info("未启用日常考勤");
            return;
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (setting.getExcludeWeekends() &&
                (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY))) {
            // 排除周末
            log.info("日常考勤排除周末，昨天是周末，不生成考勤明细");
            return;
        }

        // 查询出当天未生成正常打卡的人员
        List<PersonBO> absencePersons = dailyAttendanceResultMapper.findAbsencePersonsOfDate(date);
        if (absencePersons.isEmpty()) {
            log.info("未查询出缺勤的人员");
            return;
        }

        AttendanceResult result;
        if (setting.getExcludeHolidays() &&
                dailyAttendanceExcludeDateMapper.isExcludeDate(date, ExcludeDateType.HOLIDAY.getValue())) {
            // 排除节假日
            log.info("日常考勤设置排除节假日，{} 是节假日，考勤结果为休息", date);
            result = AttendanceResult.REST;
        } else if (setting.getExcludeCustomDates() &&
                dailyAttendanceExcludeDateMapper.isExcludeDate(date, ExcludeDateType.CUSTOM_DATE.getValue())) {
            // 排除自定义节假日
            log.info("日常考勤设置排除自定义节假日，{} 是自定义节假日，考勤结果为休息", date);
            result = AttendanceResult.REST;
        } else {
            result = AttendanceResult.ABSENCE;
        }

        // 生成考勤记录
        long startedAt = System.currentTimeMillis();
        log.info("开始为 {} 个人生成考勤结果", absencePersons.size());

        LocalDateTime now = LocalDateTime.now();
        Lists.partition(absencePersons, 1000)// 每次为1000人生成结果
                .forEach(persons -> {
                    List<String> personNos = persons
                            .stream()
                            .map(PersonBO::getPersonNo)
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

                    List<DailyAttendanceResultDO> results = persons
                            .stream()
                            .map(person -> {
                                DailyAttendanceResultDO record = new DailyAttendanceResultDO();
                                record.setId(IdGenerateUtil.getId());

                                if (leavingPersons.contains(person.getPersonNo())) {
                                    record.setResult(AttendanceResult.LEAVE.getType());
                                } else if (practicePersons.contains(person.getPersonNo())) {
                                    record.setResult(AttendanceResult.PRACTICE.getType());
                                } else if (normalPersons.contains(person.getPersonNo())) {
                                    record.setResult(AttendanceResult.NORMAL.getType());
                                } else {
                                    record.setResult(result.getType());
                                }

                                record.setAttendanceDate(date);
                                record.setPersonNo(person.getPersonNo());
                                record.setPersonName(person.getPersonName());
                                record.setOrgIndex(person.getOrgIndex());
                                record.setOrgIndexPath(person.getOrgIndexPath());// 加上人员所在组织本级
                                record.setCreatedAt(now);
                                return record;
                            }).collect(Collectors.toList());
                    dailyAttendanceResultService.batchSave(date, results);
                });

        log.info("生成 {} 的日常考勤缺勤结果，耗时：{}s <===", date, (System.currentTimeMillis() - startedAt) / 1000);
    }
}
