package com.unisinsight.business.job;

import com.unisinsight.business.bo.PartitionRange;
import com.unisinsight.business.mapper.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

/**
 * 分表定时任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/22
 * @since 1.0
 */
@Component
@Slf4j
@DependsOn("flywayInitializer")
public class TablePartitionJob implements InitializingBean {

    @Resource
    private OriginalRecordMapper originalRecordMapper;

    @Resource
    private DailyAttendanceResultMapper dailyAttendanceResultMapper;

    @Resource
    private DailyAttendanceWeekResultMapper dailyAttendanceWeekResultMapper;

    @Resource
    private DailyAttendancePeriodStuMapper dailyAttendancePeriodStuMapper;

    @Resource
    private CommonTableMapper commonTableMapper;

    private static final int PARTITION_BY_WEEK = 1;
    private static final int PARTITION_BY_MONTH = 2;

    /**
     * 获取需要分区的表
     */
    public PartitionByTimeTable[] getNeedPartitionTables() {
        return new PartitionByTimeTable[]{
                new PartitionByTimeTable("original_records", null,
                        PARTITION_BY_WEEK, originalRecordMapper),
                new PartitionByTimeTable("daily_attendance_results", null,
                        PARTITION_BY_WEEK, dailyAttendanceResultMapper),
                new PartitionByTimeTable("daily_attendance_week_results", null,
                        PARTITION_BY_WEEK, dailyAttendanceWeekResultMapper),
                new PartitionByTimeTable("daily_attendance_static_period_stu",
                        null,
                        PARTITION_BY_MONTH, dailyAttendancePeriodStuMapper)
        };
    }

    /**
     * 每天 00:05 触发
     */
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void partitionTable() {
        log.info("[定时任务] - 创建分区表开始 ===>");

        PartitionByTimeTable[] tables = getNeedPartitionTables();
        if (tables.length == 0) {
            return;
        }

        LocalDate today = LocalDate.now();
        for (PartitionByTimeTable table : tables) {
            // 创建下一个周期的子表
            LocalDate partitionedLatestDate = commonTableMapper.getPartitionedLatestDate(table.tableName);

            if (partitionedLatestDate != null && partitionedLatestDate.isAfter(today)) {
                log.info("{} 已分区到 {}，并且在今天之后，今天无需创建", table.tableName, partitionedLatestDate);
            } else {
                partitionTable(table, today);
            }
        }

        log.info("[定时任务] - 创建分区表完成 <===");
    }

    @Override
    public void afterPropertiesSet() {
        PartitionByTimeTable[] tables = getNeedPartitionTables();
        if (tables.length == 0) {
            return;
        }

        LocalDate today = LocalDate.now();
        for (PartitionByTimeTable table : tables) {
            // 已分区的最大时间
            LocalDate partitionedLatestDate = commonTableMapper.getPartitionedLatestDate(table.tableName);
            if (partitionedLatestDate != null && partitionedLatestDate.isAfter(today)) {
                log.info("{} 已分区到 {}，并且在开始时间 {} 之后，无需创建子表", table.tableName,
                        partitionedLatestDate, today);
            } else {
                // 创建从 startDate 到下一个周期的子表
                partitionTable(table, today);
            }

            // 是否需要迁移数据，并且旧表存在
            boolean needMigrate = StringUtils.isNotEmpty(table.migrateFromTableName) &&
                    commonTableMapper.tableExists(table.migrateFromTableName);

            if (needMigrate) {
                // 如果需要迁移数据，开始时间取旧表最早的时间
                LocalDateTime dataEarliestTime = table.tableMapper.getEarliestPartitionTime();
                if (dataEarliestTime != null) {
                    LocalDate dataStartDate = dataEarliestTime.toLocalDate();
                    //查询分区表最早的时间
                    LocalDate partitionedEarliestDate = commonTableMapper.getPartitionedEarliestDate(table.tableName);
                    if( partitionedEarliestDate.isAfter(dataStartDate)){
                        partitionTable(table, dataStartDate);
                    }
                }
                //如果需要迁移数据，
                table.tableMapper.migrateFromOldTable();
                log.info("从旧表 {} 迁移 到 {} 完成", table.migrateFromTableName, table.tableName);
            }
        }
    }

    /**
     * 创建开始时间到下一个周期的子表
     */
    private void partitionTable(PartitionByTimeTable table, LocalDate startDate) {
        if (table.partitionRangeType == PARTITION_BY_WEEK) {
            partitionTableByWeek(table, startDate, startDate.plusWeeks(1));
        } else if (table.partitionRangeType == PARTITION_BY_MONTH) {
            partitionTableByMonth(table, startDate, startDate.plusMonths(1));
        }
    }

    /**
     * 按周创建子表
     */
    public void partitionTableByWeek(PartitionByTimeTable table, LocalDate startDate, LocalDate endDate) {
        int weeks = (int) startDate.until(endDate, ChronoUnit.WEEKS) + 1;
        log.info("按周分表: {} ，从 {} 到 {}，一共 {} 周", table.tableName, startDate, endDate, weeks);

        PartitionRange[] ranges = new PartitionRange[weeks];
        for (int i = 0; i < weeks; i++) {
            ranges[i] = PartitionRange.genPartitionRangeByWeek(endDate.minusWeeks(i));
        }
        partitionTables(table, ranges);
    }

    /**
     * 按月创建子表
     */
    public void partitionTableByMonth(PartitionByTimeTable table, LocalDate startDate, LocalDate endDate) {
        //都以月初计算
        LocalDate startDay = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDay = endDate.with(TemporalAdjusters.firstDayOfMonth());

        int months = (int) startDay.until(endDay, ChronoUnit.MONTHS) + 1;
        log.info("按月分表: {}，从 {} 到 {}，一共 {} 月", table.tableName, startDate, endDate, months);

        PartitionRange[] ranges = new PartitionRange[months];
        for (int i = 0; i <  months; i++) {
            LocalDate localDate = endDate.minusMonths(i);
            ranges[i] = PartitionRange.genPartitionRangeByMonth(localDate);
        }
        partitionTables(table, ranges);
    }

    /**
     * 创建子表
     */
    private void partitionTables(PartitionByTimeTable table, PartitionRange[] ranges) {
        // 查询已存在的所有子表
        Set<String> childTables = commonTableMapper.getChildTables(table.tableName);

        for (PartitionRange range : ranges) {
            // 过滤已创建的表
            String childTableName = table.tableName + range.getTableSuffix();
            if (childTables.contains(childTableName)) {
                log.info("{} 已创建，跳过", childTableName);
                continue;
            }

            table.tableMapper.createPartitionTable(range.getTableSuffix(), range.getStartDate(),
                    range.getEndDate().plusDays(1));// 子表数据包括开始日期，不包括结束日期
            log.info("创建分区子表 {}", childTableName);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class PartitionByTimeTable {
        /**
         * 分区表名
         */
        private String tableName;

        /**
         * 需要迁移的旧表名
         */
        private String migrateFromTableName;

        /**
         * 分区时间间隔类型
         */
        private int partitionRangeType;

        /**
         * 实现了创建分区子表、迁移数据等方法的mapper
         */
        private PartitionByTimeTableMapper tableMapper;
    }
}
