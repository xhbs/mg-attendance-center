package com.unisinsight.business.bo;


import lombok.Data;

import javax.annotation.Nullable;

import java.time.LocalDate;



@Data
public class SubsidAttendanceResultBO{
    /**
     * 主键
     */
    private Long id;

    /**
     * 状态：0在校 99缺勤
     */
    private Short result;

    /**
     * 学生编号
     */
    private String personNo;

    /**
     * 学生姓名
     */
    private String personName;

    /**
     * 组织编码
     */
    private String orgIndex;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 考勤周期-开始日期
     */
    private LocalDate attendanceStartDate;

    /**
     * 考勤周期-结束日期
     */
    private LocalDate attendanceEndDate;

    /**
     * 周一考勤结果
     */
    @Nullable
    private Short resultOfMonday;

    /**
     * 周二考勤结果
     */
    @Nullable
    private Short resultOfTuesday;

    /**
     * 周三考勤结果
     */
    @Nullable
    private Short resultOfWednesday;

    /**
     * 周四考勤结果
     */
    @Nullable
    private Short resultOfThursday;

    /**
     * 周五考勤结果
     */
    @Nullable
    private Short resultOfFriday;


}
