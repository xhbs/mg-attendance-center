/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.response;

import com.unisinsight.business.dto.request.SubsidRosterInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 20:33:05
 * @since 1.0
 */
@Data
public class SubsidBaseResDTO implements Serializable {

    @ApiModelProperty(value = "比对规则id" )
    private Integer subsidRuleId;

    @ApiModelProperty(value = "名单标识" )
    private String subListIndex;

    public SubsidBaseResDTO(Integer subsidRuleId) {
        this.subsidRuleId = subsidRuleId;
    }

    public SubsidBaseResDTO(Integer subsidRuleId, String subListIndex) {
        this.subsidRuleId = subsidRuleId;
        this.subListIndex = subListIndex;
    }
}
