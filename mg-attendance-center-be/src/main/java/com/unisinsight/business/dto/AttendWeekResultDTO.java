package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 日常考勤周考勤结果表
 *
 * @author jiangnan [jiang.nan@unisinsight.com]
 * @date 2021/8/17
 */
@Data
public class AttendWeekResultDTO {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 状态：0在校 99缺勤
     */
    @ApiModelProperty(value = "状态")
    private Integer result;
    /**
     * 学生编号
     */
    @ApiModelProperty(value = "学生编号")
    private String personNo;

    /**
     * 学生姓名
     */
    @ApiModelProperty(value = "学生姓名")
    private String personName;
    /**
     * 学生照片
     */
    @ApiModelProperty(value = "学生照片")
    private String personUrl;
    /**
     * 组织索引
     */
    @ApiModelProperty(value = "组织索引")
    private String orgIndex;

    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码")
    private String orgIndexPath;
    /**
     * 考勤周期-开始日期
     */
    @ApiModelProperty(value = "考勤周期-开始日期")
    private LocalDate attendanceStartDate;

    /**
     * 考勤周期-结束日期
     */
    @ApiModelProperty(value = "考勤周期-结束日期")
    private LocalDate attendanceEndDate;
    /**
     * 周一考勤结果
     */
    @ApiModelProperty(value = "周一考勤结果")
    private Integer resultOfMonday;

    /**
     * 周二考勤结果
     */
    @ApiModelProperty(value = "周二考勤结果")
    private Integer resultOfTuesday;

    /**
     * 周三考勤结果
     */
    @ApiModelProperty(value = "周三考勤结果")
    private Integer resultOfWednesday;

    /**
     * 周四考勤结果
     */
    @ApiModelProperty(value = "周四考勤结果")
    private Integer resultOfThursday;

    /**
     * 周五考勤结果
     */
    @ApiModelProperty(value = "周五考勤结果")
    private Integer resultOfFriday;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String describe;

}
