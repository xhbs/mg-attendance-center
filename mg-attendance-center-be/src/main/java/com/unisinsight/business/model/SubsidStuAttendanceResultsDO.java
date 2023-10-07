package com.unisinsight.business.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "subsid_stu_attendance_results")
public class SubsidStuAttendanceResultsDO {
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 状态：0在校 99缺勤
     */
    @Column(name = "result")
    private Short result;

    /**
     * 学生编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 学生姓名
     */
    @Column(name = "person_name")
    private String personName;

    /**
     * 组织编号
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 考勤类型
     */
    @Column(name = "attendance_type")
    private Short attendanceType;

    /**
     * 考勤名称
     */
    @Column(name = "task_name")
    private String taskName;
    /**
     * 考勤周期-开始日期
     */
    @Column(name = "attendance_start_date")
    private LocalDate attendanceStartDate;

    /**
     * 考勤周期-结束日期
     */
    @Column(name = "attendance_end_date")
    private LocalDate attendanceEndDate;

    /**
     * 周一考勤结果
     */
    @Column(name = "result_of_monday")
    private Short resultOfMonday;

    /**
     * 周二考勤结果
     */
    @Column(name = "result_of_tuesday")
    private Short resultOfTuesday;

    /**
     * 周三考勤结果
     */
    @Column(name = "result_of_wednesday")
    private Short resultOfWednesday;

    /**
     * 周四考勤结果
     */
    @Column(name = "result_of_thursday")
    private Short resultOfThursday;

    /**
     * 周五考勤结果
     */
    @Column(name = "result_of_friday")
    private Short resultOfFriday;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 资助比对规则id
     */
    @Column(name = "subsid_rule_id")
    private Integer subsidRuleId;
    /**
     * 关联任务id
     */
    @Column(name = "task_rel_id")
    private Integer taskRelId;

    @Column(name = "month")
    private String month;
}