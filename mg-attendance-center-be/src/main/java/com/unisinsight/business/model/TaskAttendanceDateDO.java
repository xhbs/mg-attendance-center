package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 抽查任务考勤日期表
 */
@Data
@Table(name = "task_attendance_dates")
public class TaskAttendanceDateDO {
    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private Integer taskId;

    /**
     * 考勤日期
     */
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;
}