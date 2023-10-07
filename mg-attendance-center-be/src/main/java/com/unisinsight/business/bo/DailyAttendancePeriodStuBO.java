package com.unisinsight.business.bo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Data
public class DailyAttendancePeriodStuBO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 学生编号
     */
    private String personNo;

    /**
     * 学生编号
     */
    private List<String> personNos;

    /**
     * 根据学生编号或者姓名模糊查询
     */
    private String likeNoOrName;

    /**
     * 学生姓名
     */
    private String personName;

    /**
     * 组织编号
     */
    private String orgIndex;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织类型
     */
    private String subType;

    /**
     * 组织编号 List
     */
    private List<String> orgIndexs;


    /**
     * 组织索引
     */
    private String orgIndexPath;

    /**
     * 组织索引
     */
    private List<String> orgIndexPaths;

    /**
     * 学年
     */
    private String schoolYear;

    /**
     * 学期（0-春季，1-秋季）
     */
    private String schoolTerm;


    /**
     * 学期（0-春季，1-秋季）
     */
    private String yearMonth;

    /**
     * 考勤周期
     */
    private Short checkWeek;

    /**
     * 正常出席周
     */
    private Short normalWeeks;

    /**
     * 缺席周
     */
    private Short absentWeeks;

    /**
     * 创建时间
     */
    private LocalDate createTime;


    private LocalDate createTimeSt;

    private LocalDate createTimeEd;

    /**
     * 更新时间
     */
    private LocalDate updateTime;


}