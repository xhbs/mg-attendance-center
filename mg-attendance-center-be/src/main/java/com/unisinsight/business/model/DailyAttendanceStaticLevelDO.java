package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
@Data
@Table(name = "daily_attendance_static_level")
public class DailyAttendanceStaticLevelDO {
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 组织标识
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 组织名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 类型
     */
    @Column(name = "sub_type")
    private Short subType;

    /**
     * 父组织路径
     */
    @Column(name = "org_parent_index")
    private String orgParentIndex;

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
     * 月份（yyyy-mm格式）
     */
    @Column(name = "year_month")
    private String yearMonth;

    /**
     * 考勤周期
     */
    @Column(name = "check_week")
    private Short checkWeek;

    /**
     * 学生总数
     */
    @Column(name = "student_num")
    private Integer studentNum;

    /**
     * 在籍学生总数
     */
    @Column(name = "regist_student_num")
    private Integer registStudentNum;

    /**
     * 人数(出席率100%)
     */
    private Integer range1;

    /**
     * 人数(出席率90-99%)
     */
    private Integer range2;

    /**
     * 人数(出席率70-89%)
     */
    private Integer range3;

    /**
     * 人数(出席率50-69%)
     */
    private Integer range4;

    /**
     * 人数(出席率30-49%)
     */
    private Integer range5;

    /**
     * 人数(出席率小于30%)
     */
    private Integer range6;

    private Integer range7;

    private Integer range8;

}