package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实习点名 学生点名详情
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@Data
public class PracticeAttendancePersonDetailsDTO {

    @ApiModelProperty(value = "实习人员记录ID", required = true)
    private String practicePersonId;

    @ApiModelProperty(value = "人员编码", required = true)
    private String personNo;

    @ApiModelProperty(value = "人员名称", required = true)
    private String personName;

    @ApiModelProperty(value = "人员图像", required = true)
    private String personUrl;

    @ApiModelProperty(value = "班级", required = true)
    private String className;

    @ApiModelProperty(value = "考勤点名开始日期, yyyy-MM-dd", required = true)
    private String attendanceStartDate;

    @ApiModelProperty(value = "考勤点名结束日期, yyyy-MM-dd", required = true)
    private String attendanceEndDate;

    @ApiModelProperty(value = "考勤日期, yyyy-MM-dd")
    private String attendanceDate;

    @ApiModelProperty(value = "实际考勤时间, yyyy-MM-dd HH:mm:ss")
    private String attendanceTime;

    @ApiModelProperty(value = "考勤结果， 0正常 99缺勤")
    private Integer attendanceResult;

    @ApiModelProperty(value = "申请人姓名")
    private String creatorName;

    @ApiModelProperty(value = "申请人角色")
    private String creatorRoleName;

    @ApiModelProperty(value = "申请时间, yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedAt;
}
