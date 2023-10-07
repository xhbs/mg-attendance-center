package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日常考勤明细
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Data
public class DailyAttendanceResultDTO {

    /**
     * 主键
     */
    @ApiModelProperty(value = "ID", required = true, example = "857409314078986256")
    private String id;

    /**
     * 状态：-1休息 0在校 99缺勤
     */
    @ApiModelProperty(name = "result", value = "状态：-1休息 0在校 99缺勤", required = true, example = "0")
    private Integer result;

    /**
     * 类型 目前都是周考勤
     */
    @ApiModelProperty(name = "result", value = "类型：1周考勤", required = true, example = "1")
    private Integer attendanceType = 1;

    /**
     * 学生编号
     */
    @ApiModelProperty(name = "person_no", value = "学生编号", required = true, example = "123456")
    private String personNo;

    /**
     * 学生姓名
     */
    @ApiModelProperty(name = "person_name", value = "学生姓名", required = true, example = "张三")
    private String personName;

    /**
     * 学年
     */
    @ApiModelProperty(name = "school_year", value = "学年", required = true, example = "2021～2020")
    private String schoolYear;

    /**
     * 学期
     */
    @ApiModelProperty(name = "semester", value = "学期", required = true, example = "秋季")
    private String semester;

    /**
     * 月份
     */
    @ApiModelProperty(name = "month", value = "月份", required = true, example = "2021年9月")
    private String month;

    /**
     * 考勤周期-开始日期
     */
    @ApiModelProperty(name = "attendance_start_date", value = "考勤周期-开始日期", required = true, example = "2021.9.5")
    private String attendanceStartDate;

    /**
     * 考勤周期-结束日期
     */
    @ApiModelProperty(name = "attendance_end_date", value = "考勤周期-结束日期", required = true, example = "2021.9.9")
    private String attendanceEndDate;

    /**
     * 周一考勤结果
     */
    @ApiModelProperty(name = "result_of_monday", value = "周一考勤结果", example = "1")
    private Integer resultOfMonday;

    /**
     * 周二考勤结果
     */
    @ApiModelProperty(name = "result_of_tuesday", value = "周二考勤结果", example = "1")
    private Integer resultOfTuesday;

    /**
     * 周三考勤结果
     */
    @ApiModelProperty(name = "result_of_wednesday", value = "周三考勤结果", example = "1")
    private Integer resultOfWednesday;

    /**
     * 周四考勤结果
     */
    @ApiModelProperty(name = "result_of_thursday", value = "周四考勤结果", example = "1")
    private Integer resultOfThursday;

    /**
     * 周五考勤结果
     */
    @ApiModelProperty(name = "result_of_friday", value = "周五考勤结果", example = "1")
    private Integer resultOfFriday;
}
