/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 20:33:05
 * @since 1.0
 */
@Data
public class SubsidBaseReqDTO {

    @ApiModelProperty(value = "比对规则id")
    private Integer subsidRuleId;

    @ApiModelProperty(value = "名单标识，如果是手动比对，则必填")
    private String subListIndex;

    @ApiModelProperty(value = "比对信息，如果是自动比对类型，则必填")
    private SubsidRosterInfo subsidRosterInfo;

    @ApiModelProperty(value = "组织标识", required = true)
    private String orgIndex;

    @ApiModelProperty(value = "学年(格式：2020-2021)")
    private String schoolYear;

    @ApiModelProperty(value = "学期（0-春季，1-秋季）")
    private String schoolTerm;

    @ApiModelProperty(value = "考勤开始时间", required = true)
    private LocalDate chkDateSt;

    @ApiModelProperty(value = "考勤结束时间", required = true)
    private LocalDate chkDateEd;

    @ApiModelProperty(value = "缺勤率", required = true)
    private Integer absentRate = 0;

    @ApiModelProperty(value = "点名缺勤率", required = true)
    private Integer callTheRollAbsentRate = 0;

    @ApiModelProperty(value = "使用的缺勤率规则 0-无感+抽查考勤,1-点名考勤", required = true)
    private Integer rule;

    @ApiModelProperty(value = "比对类型，0-自动比对，1-手动比对", required = true)
    private Short subsidType;

}
