package com.unisinsight.business.bo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

@Data
public class DailyAttendanceStaticLevelBO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 组织标识
     */
    private String orgIndex;

    /**
     * 组织类型
     */
    private Short subType;

    /**
     * 组织标识List
     */
    private List<String> orgIndexs;

    /**
     * 组织名称
     */
    private String orgName;
    /**
     * 父组织标识
     */
    private String orgParentIndex;

    /**
     * 父组织标识 List
     */
    private List<String> orgParentIndexs;


    /**
     * 学年
     */
    private String schoolYear;

    /**
     * 学期（0-春季，1-秋季）
     */
    private String schoolTerm;

    /**
     * 月份（yyyy-mm格式）
     */
    private String yearMonth;

    /**
     * 考勤周期
     */
    private Short checkWeek;

    /**
     * 学生总数
     */
    private Integer studentNum;

    /**
     * 在籍学生总数
     */
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