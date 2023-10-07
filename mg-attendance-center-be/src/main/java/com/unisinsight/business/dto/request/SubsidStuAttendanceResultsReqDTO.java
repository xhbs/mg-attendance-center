/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:04:00
 * @since 1.0
 */
@Data
public class SubsidStuAttendanceResultsReqDTO  extends PageParam {

    @ApiModelProperty(value = "学生编号",required = true)
    private String personNo;

    @ApiModelProperty(value = "资助比对规则id",required = true)
    private Integer subsidRuleId;

}
