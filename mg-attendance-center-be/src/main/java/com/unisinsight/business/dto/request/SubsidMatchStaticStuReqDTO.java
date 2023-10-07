/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 15:05:21
 * @since 1.0
 */
@Data
@ApiModel(value = "资助比对学生统计查询")
public class SubsidMatchStaticStuReqDTO extends PageParam {

    @ApiModelProperty(value = "组织标识")
    private String orgIndex;

    @ApiModelProperty(value = "父组织标识",required = true)
    private String orgParentIndex;

    @ApiModelProperty(value = "资助比对规则id",required = true)
    private Integer subsidRuleId;

    @ApiModelProperty(value = "缺勤率",required = true)
    private Integer absentRate;

    @ApiModelProperty(value = "比对状态，0-未通过，1-通过",example = "0")
    private String status;

    @ApiModelProperty(value = "考勤开始时间",required = true)
    private Date chkDateSt;

    @ApiModelProperty(value = "考勤结束时间",required = true)
    private Date chkDateEd;

    @ApiModelProperty(value = "模糊查询（学生姓名/班级/学号）")
    private String searchKey;

}
