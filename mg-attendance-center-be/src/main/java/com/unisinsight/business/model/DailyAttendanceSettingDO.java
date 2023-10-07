package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 日常考勤设置表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@Data
@Table(name = "daily_attendance_setting")
public class DailyAttendanceSettingDO {

    /**
     * 是否启用
     */
    @Column(name = "enable")
    private Boolean enable;

    /**
     * 是否排除排除周末
     */
    @Column(name = "exclude_weekends")
    private Boolean excludeWeekends;

    /**
     * 是否排除节假日
     */
    @Column(name = "exclude_holidays")
    private Boolean excludeHolidays;

    /**
     * 是否排除自定义日期
     */
    @Column(name = "exclude_custom_dates")
    private Boolean excludeCustomDates;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 更新人编号
     */
    @Column(name = "updated_by")
    private String updatedBy;
}
