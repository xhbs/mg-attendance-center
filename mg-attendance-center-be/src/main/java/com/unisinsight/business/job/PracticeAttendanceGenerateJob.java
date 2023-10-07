package com.unisinsight.business.job;

import com.unisinsight.business.bo.OriginalRecordBO;
import com.unisinsight.business.bo.PracticeAttendancePersonBO;
import com.unisinsight.business.bo.PracticeAttendanceResultBO;
import com.unisinsight.business.common.constants.SystemConfigs;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.mapper.PracticePersonMapper;
import com.unisinsight.business.mapper.PracticeRecordMapper;
import com.unisinsight.business.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
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
 * 实习点名考勤结果处理
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/14
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class PracticeAttendanceGenerateJob implements OriginalRecordConsumer, ApplicationRunner {

    @Resource
    private PracticePersonMapper practicePersonMapper;

    @Resource
    private PracticeRecordMapper practiceRecordMapper;

    @Resource
    private SystemConfigService systemConfigService;

    /**
     * 实习点名任务天数
     */
    private Integer practiceAttendanceDayCount = 7;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void consume(LocalDate date, List<OriginalRecordBO> records) {
//        Map<String, OriginalRecordBO> recordMap = new HashMap<>(records.size());
//        for (OriginalRecordBO record : records) {
//            recordMap.put(record.getPersonNo(), record);
//        }
//
//        // 查找出有实习点名任务的人员
//        LocalDate attendanceEndDate = date.plusDays(practiceAttendanceDayCount);// 实习申请提前14天发起，前面7天进行实习点名
//        List<PracticeAttendancePersonBO> persons = practicePersonMapper.findHaveTaskPersons(recordMap.keySet(),
//                date, attendanceEndDate);
//        if (persons.isEmpty()) {
//            log.info("未找到在 {} - {} 有实习点名任务的人员", date, attendanceEndDate);
//            return;
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        List<PracticeAttendanceResultBO> results = persons
//                .stream()
//                .map(src -> {
//                    PracticeAttendanceResultBO dst = new PracticeAttendanceResultBO();
//                    dst.setPracticePersonId(src.getPracticePersonId());
//                    dst.setAttendanceResult(AttendanceResult.NORMAL.getType());
//                    dst.setAttendanceTime(recordMap.get(src.getPersonNo()).getPassTime());
//                    return dst;
//                }).collect(Collectors.toList());
//        practicePersonMapper.updateAttendanceResults(results);
//
//        // 实习记录ID集合
//        List<Integer> practiceRecordIds = persons
//                .stream()
//                .map(PracticeAttendancePersonBO::getPracticeRecordId)
//                .distinct()
//                .collect(Collectors.toList());
//        // 更新实习申请的考勤状态
//        practiceRecordMapper.updatePassedAttendanceRecords(practiceRecordIds);
//
//        Duration duration = Duration.between(now, LocalDateTime.now());
//        log.info("保存 {} 条实习点名结果，耗时：{} ms <===", results.size(), duration.toMillis());
    }

    @Override
    public void run(ApplicationArguments args) {
        practiceAttendanceDayCount = systemConfigService
                .getIntegerConfigValue(SystemConfigs.PRACTICE_ATTENDANCE_DAY_COUNT);
    }
}
