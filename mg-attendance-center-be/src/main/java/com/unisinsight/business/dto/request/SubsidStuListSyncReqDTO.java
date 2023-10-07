/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:04:50
 * @since 1.0
 */
@Data
public class SubsidStuListSyncReqDTO implements Serializable {

    @ApiModelProperty(name = "yearMonth", value = "年月：yyyy-MM", example = "2021-01",required = true)
    private String yearMonth;

    @ApiModelProperty(name = "proType", value = "项目类型（0-免学费，1-国家助学金）", example = "0",required = true)
    private Short proType;

    @ApiModelProperty(name = "dataType", value = "数据类型（0-主管审核名单，1-学校审核名单）", example = "0",required = true)
    private Short dataType;

    @NotEmpty
    @ApiModelProperty(name = "stulist", value = "学生信息数组", example = "0",required = true)
    private List<SubsidStuInfoDTO> stulist;


}
