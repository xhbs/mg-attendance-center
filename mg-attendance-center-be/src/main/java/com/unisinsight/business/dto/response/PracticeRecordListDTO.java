package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实习记录 分页查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@Data
public class PracticeRecordListDTO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Integer id;

    /**
     * 审批状态
     */
    @ApiModelProperty(value = "审批状态： 1未上报 2审批中 3同意 4拒绝")
    private Integer status;

    /**
     * 实习点名状态
     */
    @ApiModelProperty(value = "实习点名状态 0进行中 1点名通过 2点名失败")
    private Integer attendanceState;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 上报时间
     */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportedAt;

    /**
     * 人员姓名集合
     */
    @ApiModelProperty(value = "人员姓名", example = "张三、王五、李六")
    private String personNames;

    /**
     * 第一个人员的图片url
     */
    @ApiModelProperty(value = "第一个人员的图片url")
    private String firstPersonUrl;

    /**
     * 实习开始时间
     */
    @ApiModelProperty(name = "start_time", value = "实习开始时间")
    @JsonProperty("start_time")
    private String startDate;

    /**
     * 实习结束时间
     */
    @ApiModelProperty(name = "end_time", value = "实习结束时间")
    @JsonProperty("end_time")
    private String endDate;

    /**
     * 上报组织路径
     */
    @ApiModelProperty(value = "上报组织路径")
    private String orgIndexPath;

    /**
     * 上报组织层级名称
     */
    @ApiModelProperty(value = "上报组织层级名称")
    private String orgPathName;

    /**
     * 上报人编号
     */
    @ApiModelProperty(value = "上报人编号")
    private String creatorCode;

    /**
     * 上报人编号
     */
    @ApiModelProperty(value = "上报人姓名")
    private String creatorName;

    /**
     * 上报人角色
     */
    @ApiModelProperty(value = "上报人角色")
    private String creatorRoleName;
}
