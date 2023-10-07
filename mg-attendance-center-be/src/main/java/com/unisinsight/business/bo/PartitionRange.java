package com.unisinsight.business.bo;

import com.unisinsight.business.common.constants.DateTimeFormats;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * 根据时间分区表时间段封装
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/3/3
 * @since 1.0
 */
@Getter
public class PartitionRange {
    /**
     * 分区键开始时间
     */
    private LocalDate startDate;

    /**
     * 分区键结束时间
     */
    private LocalDate endDate;

    private PartitionRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 生成子表后缀
     *
     * @return 例如 _20210222_20210228
     */
    public String getTableSuffix() {
        return "_" + startDate.format(DateTimeFormats.yyyyMMdd) + "_" + endDate.format(DateTimeFormats.yyyyMMdd);
    }

    /**
     * 根据指定时间生成所在周的时间段
     */
    public static PartitionRange genPartitionRangeByWeek(LocalDate date) {
        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate sunday = date.with(DayOfWeek.SUNDAY);
        return new PartitionRange(monday, sunday);
    }

    /**
     * 根据指定时间生成所在月的时间段
     */
    public static PartitionRange genPartitionRangeByMonth(LocalDate date) {
        LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return new PartitionRange(firstDay, lastDay);
    }
}
