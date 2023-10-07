package com.unisinsight.business.job;

import com.unisinsight.business.common.constants.SystemConfigs;
import com.unisinsight.business.mapper.PracticePersonMapper;
import com.unisinsight.business.mapper.PracticeRecordMapper;
import com.unisinsight.business.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实习点名缺勤结果生成
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/14
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class PracticeAttendanceAbsenceJob implements AttendanceAbsenceJob {

    @Resource
    private PracticePersonMapper practicePersonMapper;

    @Resource
    private PracticeRecordMapper practiceRecordMapper;

    @Resource
    private SystemConfigService systemConfigService;

    @Scheduled(cron = "${cron.PracticeAttendanceAbsenceJob}")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run() {
        log.info("[定时任务] - 生成实习点名缺勤结果 ===>");

        Integer dayCount = systemConfigService.getIntegerConfigValue(SystemConfigs.PRACTICE_ATTENDANCE_DAY_COUNT);
        generate(LocalDate.now().plusDays(dayCount));// 实习申请提前14天发起，前面7天进行实习点名
    }

    /**
     * 生成某一天的缺勤结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generate(LocalDate date) {
        List<Integer> recordIds = practiceRecordMapper.findAbsenceFinishedRecords(date);
        log.info("在 {} 有 {} 条实习点名任务已结束", date, recordIds.size());

        if (recordIds.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        practicePersonMapper.updateAbsenceResults(recordIds, now);
        log.info("生成 {} 条实习申请关联的实习人员缺勤结果", recordIds.size());

        practiceRecordMapper.updateFailedAttendanceRecords(recordIds);
        Duration duration = Duration.between(now, LocalDateTime.now());
        log.info("更新 {} 条实习申请的实习点名状态，耗时：{} ms <===", recordIds.size(), duration.toMillis());
    }
}
