package com.unisinsight.business.model;

import com.unisinsight.business.bo.PartitionRange;
import lombok.Data;

import java.time.LocalDate;
import javax.persistence.*;

@Table(name = "daily_attendance_static_period_stu")
@Data
public class DailyAttendancePeriodStuDO implements PartitionTable{
    /**
     * 主键
     */
    @Id
    private Long id;

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
     * 组织名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 学年
     */
    @Column(name = "school_year")
    private String schoolYear;

    /**
     * 学期（0-春季，1-秋季）
     */
    @Column(name = "school_term")
    private String schoolTerm;


    /**
     * 学期（0-春季，1-秋季）
     */
    @Column(name = "year_month")
    private String yearMonth;

    /**
     * 考勤周期
     */
    @Column(name = "check_week")
    private Short checkWeek;

    /**
     * 正常出席周
     */
    @Column(name = "normal_weeks")
    private Short normalWeeks;

    /**
     * 缺席周
     */
    @Column(name = "absent_weeks")
    private Short absentWeeks;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDate createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDate updateTime;

    @Override
    public String getTableSuffix() {
        if (createTime == null) {
            return null;
        }
        return PartitionRange.genPartitionRangeByMonth(createTime).getTableSuffix();
    }
}