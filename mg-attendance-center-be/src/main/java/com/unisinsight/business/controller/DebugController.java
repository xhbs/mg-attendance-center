package com.unisinsight.business.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.job.*;
import com.unisinsight.business.manager.SMSManager;
import com.unisinsight.business.mq.event.DispositionEvent;
import com.unisinsight.business.service.DispositionService;
import com.unisinsight.framework.common.util.date.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/1/19
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/hooks")
@Api(tags = "提供给测试的一些调试接口")
@Slf4j
public class DebugController {

    // ------------- 触发定时任务 start -------------

    @Resource
    private DailyAttendanceAbsenceJob dailyAttendanceAbsenceJob;

    @GetMapping("/daily-absence-for-yesterday")
    @ApiOperation("日常 - 生成昨天的缺勤记录")
    public void dailyAbsenceForYesterday() {
        dailyAttendanceAbsenceJob.run();
    }

    @GetMapping("/daily-absence")
    @ApiOperation("日常 - 生成一段时间内的缺勤记录")
    public void dailyAbsence(@RequestParam("start_date")
                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                             @NotNull LocalDate startDate,
                             @RequestParam("end_date")
                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                             @NotNull LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("开始日期不能晚于结束日期");
        }
        if (endDate.isAfter(LocalDate.now())) {
            throw new InvalidParameterException("结束日期不能晚于今天");
        }

        while (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
            dailyAttendanceAbsenceJob.generate(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    @Resource
    private DailyAttendanceAlertJob dailyAttendanceAlertJob;

    @GetMapping("/daily-attendance-alert")
    @ApiOperation("日常 - 每周五发送短信提醒班主任有感考勤")
    public void dailyAttendanceAlert() {
        dailyAttendanceAlertJob.run();
    }

    @Resource
    private DailyAttendanceHighLvlInputDataJob dailyAttendanceHighLvlInputDataJob;

    @Resource
    private DailyAttendancePeriodStuInputDataJob dailyAttendancePeriodStuInputDataJob;

    @GetMapping("/daily-stat-person")
    @ApiOperation("日常统计 - 生成按学生统计数据")
    public void dailyStatPerson() {
        dailyAttendancePeriodStuInputDataJob.run();
    }

    @GetMapping("/daily-stat-org")
    @ApiOperation("日常统计 - 生成按组织统计数据")
    public void dailyStatOrg() {
        dailyAttendanceHighLvlInputDataJob.run();
    }

    @Resource
    private SpotAttendanceAbsenceJob spotAttendanceAbsenceJob;

    @GetMapping("/spot-absence-for-yesterday")
    @ApiOperation("抽查 - 生成昨天的缺勤记录")
    public void spotAbsenceForYesterday() {
        spotAttendanceAbsenceJob.run();
    }

    @GetMapping("/spot-absence")
    @ApiOperation("抽查 - 生成一段时间内的缺勤记录")
    public void spotAbsence(@RequestParam("start_date")
                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                            @NotNull LocalDate startDate,
                            @RequestParam("end_date")
                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                            @NotNull LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("开始日期不能晚于结束日期");
        }
        if (endDate.isAfter(LocalDate.now())) {
            throw new InvalidParameterException("结束日期不能晚于今天");
        }

        while (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
            spotAttendanceAbsenceJob.generate(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    @Resource
    private SpotCheckTaskAlertJob spotCheckTaskAlertJob;

    @GetMapping("/spot-task-alert")
    @ApiOperation("抽查 - 给班主任发送有感考勤提醒")
    public void spotTaskAlert() {
        spotCheckTaskAlertJob.run();
    }

    @Resource
    private SpotAttendanceStatJob spotAttendanceStatJob;

    @GetMapping("/spot-stat-for-yesterday")
    @ApiOperation("抽查统计 - 生成昨天的统计数据")
    public void spotStatForYesterday() {
        spotAttendanceStatJob.run();
    }

    @GetMapping("/spot-stat")
    @ApiOperation("抽查统计 - 生成一段时间内的统计数据")
    public void spotStatJob(@RequestParam("start_date")
                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                            @NotNull LocalDate startDate,
                            @RequestParam("end_date")
                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                            @NotNull LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("开始日期不能晚于结束日期");
        }
        if (endDate.isAfter(LocalDate.now())) {
            throw new InvalidParameterException("结束日期不能晚于今天");
        }

        while (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
            spotAttendanceStatJob.generate(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    @Resource
    private PracticeAttendanceAbsenceJob practiceAttendanceAbsenceJob;

    @GetMapping("/practice-absence-for-yesterday")
    @ApiOperation("实习点名 - 生成截至今天的实习点名缺勤结果")
    public void practiceAbsenceForYesterday() {
        practiceAttendanceAbsenceJob.run();
    }


    @GetMapping("/practice-absence")
    @ApiOperation("实习点名 - 生成一段时间内的缺勤结果")
    public void practiceAbsence(@RequestParam("start_date")
                                @DateTimeFormat(pattern = "yyyy-MM-dd")
                                @NotNull LocalDate startDate,
                                @RequestParam("end_date")
                                @DateTimeFormat(pattern = "yyyy-MM-dd")
                                @NotNull LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("开始日期不能晚于结束日期");
        }
        if (endDate.isAfter(LocalDate.now())) {
            throw new InvalidParameterException("结束日期不能晚于今天");
        }

        while (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
            practiceAttendanceAbsenceJob.generate(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    // ------------- 状态更新 end -------------
    @Resource
    private UpdateLeaveStateJob updateLeaveStateJob;

    @GetMapping("/update-leave-state")
    @ApiOperation("更新 - 请假状态")
    public void updateLeaveStateJob() {
        updateLeaveStateJob.run();
    }

    @Resource
    private UpdatePracticeStatusJob updatePracticeStatusJob;

    @GetMapping("/update-practice-status")
    @ApiOperation("更新 - 实习状态")
    public void updatePracticeStatusJob() {
        updatePracticeStatusJob.run();
    }

    @Resource
    private UpdateTaskStateJob updateTaskStateJob;

    @GetMapping("/update-task-state")
    @ApiOperation("更新 - 抽查任务状态")
    public void updateTaskStateJob() {
        updateTaskStateJob.run();
    }
    // ------------- 状态更新 end -------------

    // ------------- 数据同步 start -------------
    @Resource
    private PersonSyncJob personSyncJob;

    @GetMapping("/sync-person")
    @ApiOperation("同步 - 考勤人员")
    public void personSyncJob() {
        personSyncJob.run();
    }

    @Resource
    private OrganizationSyncJob organizationSyncJob;

    @GetMapping("/sync-organization")
    @ApiOperation("同步 - 组织树")
    public void organizationSyncJob() {
        organizationSyncJob.run();
    }

    @Resource
    private RefreshChannelCacheJob refreshChannelCacheJob;

    @GetMapping("/sync-channel")
    @ApiOperation("同步 - 通道缓存")
    public void refreshChannelCacheJob() {
        refreshChannelCacheJob.run();
    }
    // ------------- 数据同步 end -------------

    // ------------- 数据模拟 start -------------
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/mock-disposition")
    @ApiOperation("模拟 - 产生一条布控告警")
    public void mockDisposition(@RequestParam(value = "idNumber", defaultValue = "61B588015FB75D445DDEA3ED9887A9AD") String idNumber,
                                @RequestParam(value = "deviceId", defaultValue = "53250300001311000001") String deviceId,
                                @RequestParam(value = "passTime", defaultValue = "2021-10-13 00:00:00") String passTime) {
        DispositionEvent event = new DispositionEvent();
        event.setIdNumber(idNumber);
        LocalDateTime time = LocalDateTime.parse(passTime, DateUtils.DATETIME_FORMATTER);
        event.setPassTime(DateUtils.toMilliseconds(time));
        event.setDeviceId(deviceId);
        String msg = null;
        try {
            msg = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send("disposition_topic", msg);
    }

    @Resource
    private SMSManager smsManager;

    @GetMapping("/mock-sms")
    @ApiOperation("模拟 - 发送短信")
    public void sendSms(@RequestParam(value = "phone") String phone,
                        @RequestParam(value = "message") String message) {
        smsManager.send(phone, message);
    }
    // ------------- 数据模拟 end -------------

    @Resource
    private TablePartitionJob tablePartitionJob;

    @GetMapping("/partition-table")
    @ApiOperation("创建分区表")
    public void partitionTable(@RequestParam(value = "table_name") String tableName,
                               @RequestParam("type") @NotNull String type,
                               @RequestParam("start_date")
                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                               @NotNull LocalDate startDate,
                               @RequestParam("end_date")
                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                               @NotNull LocalDate endDate) {
        TablePartitionJob.PartitionByTimeTable[] tables = tablePartitionJob.getNeedPartitionTables();
        TablePartitionJob.PartitionByTimeTable table = null;
        for (TablePartitionJob.PartitionByTimeTable item : tables) {
            if (item.getTableName().equals(tableName)) {
                table = item;
                break;
            }
        }

        if (table == null) {
            throw new InvalidParameterException("分区表不存在");
        }

        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            if ("month".equals(type)) {
                LocalDate firstDay = startDate.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate lastDay = startDate.with(TemporalAdjusters.lastDayOfMonth());
                tablePartitionJob.partitionTableByMonth(table, firstDay, lastDay);
                startDate = startDate.plusMonths(1);
            } else if ("week".equals(type)) {
                LocalDate monday = startDate.with(DayOfWeek.MONDAY);
                LocalDate sunday = startDate.with(DayOfWeek.SUNDAY);
                tablePartitionJob.partitionTableByWeek(table, monday, sunday);
                startDate = startDate.plusWeeks(1);
            }
        }
    }

    // ------------- 考勤布控 start -------------

    @Resource
    private DispositionService dispositionService;

    @GetMapping("/create-dispositions")
    @ApiOperation("考勤布控 - 创建")
    public void add(@RequestParam("tab_ids") String tabIds) {
        dispositionService.createDispositions(Arrays.asList(tabIds.split(",")));
    }

    @DeleteMapping("/delete-dispositions")
    @ApiOperation("考勤布控 - 删除")
    public void delete(@RequestParam("tab_ids") String tabIds) {
        dispositionService.deleteDispositions(Arrays.asList(tabIds.split(",")));
    }
    // ------------- 考勤布控 end -------------

    // ------------- 数据清理 start -------------
    @Resource
    private SubsidClearDataDataJob subsidClearDataDataJob;

    @GetMapping("/subsidClearDataDataJob")
    @ApiOperation("数据清理 - 资助名单")
    public void subsidClearDataDataJob() {
        subsidClearDataDataJob.run();
    }

    // ------------- 数据清理 end -------------
}
