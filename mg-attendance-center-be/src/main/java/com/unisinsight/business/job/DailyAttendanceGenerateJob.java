package com.unisinsight.business.job;

import com.unisinsight.business.bo.OriginalRecordBO;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.enums.ExcludeDateType;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.mapper.DailyAttendanceExcludeDateMapper;
import com.unisinsight.business.model.DailyAttendanceResultDO;
import com.unisinsight.business.model.DailyAttendanceSettingDO;
import com.unisinsight.business.service.DailyAttendanceResultService;
import com.unisinsight.business.service.DailyAttendanceSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据打卡记录生成考勤明细
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Component
@Slf4j
public class DailyAttendanceGenerateJob implements OriginalRecordConsumer {

    @Resource
    private DailyAttendanceSettingService attendanceSettingService;

    @Resource
    private DailyAttendanceExcludeDateMapper dailyAttendanceExcludeDateMapper;

    @Resource
    private DailyAttendanceResultService dailyAttendanceResultService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void consume(LocalDate date, List<OriginalRecordBO> records) {
        DailyAttendanceSettingDO setting = attendanceSettingService.getSetting();
        if (setting == null) {
            log.warn("未找到日常考勤设置");
            return;
        }

        if (!setting.getEnable()) {
            log.info("未启用日常考勤");
            return;
        }

        if (!checkAttendanceDate(setting, date)) {
            log.info("{} 不在日常考勤日期内", date);
            return;
        }

        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        List<DailyAttendanceResultDO> results = records
                .stream()
                .map(record -> {
                    DailyAttendanceResultDO dayResult = new DailyAttendanceResultDO();
                    dayResult.setId(IdGenerateUtil.getId());
                    dayResult.setResult(AttendanceResult.NORMAL.getType());
                    dayResult.setAttendanceDate(record.getAttendanceDate());
                    dayResult.setCapturedAt(record.getPassTime());
                    dayResult.setOriginalRecordId(record.getId());
                    dayResult.setPersonNo(record.getPersonNo());
                    dayResult.setPersonName(record.getPersonName());
                    dayResult.setOrgIndex(record.getOrgIndex());
                    dayResult.setOrgIndexPath(record.getOrgIndexPath());// 加上人员所在组织本级
                    dayResult.setCreatedAt(now);
                    return dayResult;
                }).collect(Collectors.toList());

        dailyAttendanceResultService.batchSave(date, results);

        Duration duration = Duration.between(now, LocalDateTime.now());
        log.info("保存 {} 条日常考勤结果，耗时：{} ms <===", records.size(), duration.toMillis());
    }

    /**
     * 校验打卡日期是否是考勤日期
     */
    private boolean checkAttendanceDate(DailyAttendanceSettingDO setting, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (setting.getExcludeWeekends() &&
                (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY))) {
            // 排除周末
            log.debug("日常考勤排除周末，{} 是周末，不生成考勤明细", date);
            return false;
        }

        if (setting.getExcludeHolidays() &&
                dailyAttendanceExcludeDateMapper.isExcludeDate(date, ExcludeDateType.HOLIDAY.getValue())) {
            // 排除节假日
            log.debug("日常考勤设置排除节假日，{} 是节假日，不生成考勤明细", date);
            return false;
        }

        if (setting.getExcludeCustomDates() &&
                dailyAttendanceExcludeDateMapper.isExcludeDate(date, ExcludeDateType.CUSTOM_DATE.getValue())) {
            // 排除自定义节假日
            log.debug("日常考勤设置排除自定义节假日，{} 自定义节假日，不生成考勤明细", date);
            return false;
        }
        return true;
    }
}
