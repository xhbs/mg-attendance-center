package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 请假记录 分页查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LeaveRecordListDTO extends ApprovalRecordDTO {

    /**
     * 请假类型
     */
    @ApiModelProperty(value = "请假类型; 1：病假，2：事假，3：实习登记，99：其他")
    private Short type;

    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因")
    private String reason;

    /**
     * 人员编号
     */
    @ApiModelProperty(value = "人员编号")
    private String personNo;

    /**
     * 人员姓名
     */
    @ApiModelProperty(value = "人员姓名")
    private String personName;

    /**
     * 人员照片
     */
    @ApiModelProperty(value = "人员照片")
    private String personUrl;

    /**
     * 班级组织编码
     */
    @ApiModelProperty(value = "班级组织编码")
    private String orgIndex;

    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称")
    private String orgName;

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
     * 上报人姓名
     */
    @ApiModelProperty(value = "上报人姓名")
    private String creatorName;

    /**
     * 上报人角色
     */
    @ApiModelProperty(value = "上报人角色")
    private String creatorRoleName;

    /**
     * 学校名称
     */
    @ApiModelProperty(value = "学校名称")
    private String schoolName;

    /**
     * 审核人姓名
     */
    @ApiModelProperty(value = "审核人姓名")
    private String approvedBy;
}
