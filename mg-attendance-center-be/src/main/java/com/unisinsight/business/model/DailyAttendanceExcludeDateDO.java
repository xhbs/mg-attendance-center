package com.unisinsight.business.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 日常考勤排除日期表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "daily_attendance_exclude_dates")
public class DailyAttendanceExcludeDateDO extends BaseModel {

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 类型：1节假日 2自定义日期
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 开始日期
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Column(name = "end_date")
    private LocalDate endDate;
}
