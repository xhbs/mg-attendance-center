/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "调整对象分组下人员res对象")
@Data
public class PersonReqDTO  extends PaginationReq implements Serializable{
    @ApiModelProperty(value = "人员编码")
    private String personNo;
    @ApiModelProperty(value = "人员名称")
    private String personName;
}
