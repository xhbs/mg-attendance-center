/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:04:00
 * @since 1.0
 */
@Data
public class SubsidStuAttendanceResultsResDTO implements Serializable {

    @ApiModelProperty(value = "记录id")
    private Long id;

    @ApiModelProperty(value = "状态：0在校 99缺勤")
    private Short result;

    @ApiModelProperty(value = "学生编号")
    private String personNo;

    @ApiModelProperty(value = "学生姓名")
    private String personName;

    @ApiModelProperty(value = "组织编号")
    private String orgIndex;

    @ApiModelProperty(value = "考勤类型(0-抽查考勤，1-日常考勤)")
    private Short attendanceType;

    @ApiModelProperty(value = "考勤任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务关联id")
    private Integer taskRelId;

    @ApiModelProperty(value = "考勤周期-开始日期")
    private LocalDate attendanceStartDate;

    @ApiModelProperty(value = "考勤周期-结束日期")
    private LocalDate attendanceEndDate;

    @ApiModelProperty(value = "周一考勤结果")
    private Short resultOfMonday;

    @ApiModelProperty(value = "周二考勤结果")
    private Short resultOfTuesday;

    @ApiModelProperty(value = "周三考勤结果")
    private Short resultOfWednesday;

    @ApiModelProperty(value = "周四考勤结果")
    private Short resultOfThursday;

    @ApiModelProperty(value = "周五考勤结果")
    private Short resultOfFriday;

    @ApiModelProperty(value = "资助比对规则id")
    private Integer subsidRuleId;


}
