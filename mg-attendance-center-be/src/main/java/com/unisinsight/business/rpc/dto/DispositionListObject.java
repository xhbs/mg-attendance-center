package com.unisinsight.business.rpc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
@Data
public class DispositionListObject {
    @JsonProperty("DispositionObject")
    public List<DispositionObject> DispositionObject;
}
