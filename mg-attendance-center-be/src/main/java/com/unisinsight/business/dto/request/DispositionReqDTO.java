package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unisinsight.business.rpc.dto.DispositionListObject;
import lombok.Data;

/**
 * MDA-1400 批量布控
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
@Data
public class DispositionReqDTO {
    @JsonProperty("DispositionListObject")
    private DispositionListObject dispositionListObject;
}
