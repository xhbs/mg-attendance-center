package com.unisinsight.business.bo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author tanggang
 * @version 1.0
 *
 * @email tang.gang@inisinsight.com
 * @date 2021/8/16 19:37
 **/
@Data
public class StaticDateBO {
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
     * 考勤统计开始日期
     */
    private LocalDate startDate;
    /**
     * 考勤统计结束日期
     */
    private LocalDate endDate;

    /**
     * 开学日期
     */
    private LocalDate schoolOpenDate;

}
