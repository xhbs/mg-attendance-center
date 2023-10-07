package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/16
 */
@Data
@ApiModel(value = "日常考勤")
public class DailyAttendanceDTO {

    @ApiModelProperty(value = "考勤开始日期，yyyy-MM-dd")
    private LocalDate startDate;

    @ApiModelProperty(value = "考勤结束日期，yyyy-MM-dd")
    private LocalDate endDate;

    @ApiModelProperty(value = "在籍学生")
    private int registeredStu;

    @ApiModelProperty(value = "在校学生")
    private int inSchool;

    @ApiModelProperty(value = "缺勤学生")
    private int absence;

    @ApiModelProperty(value = "请假学生")
    private int leave;

    @ApiModelProperty(value = "实习学生")
    private int practice;
}
