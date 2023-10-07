package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unisinsight.business.rpc.dto.ResponseStatus;
import lombok.Data;

import java.util.List;

/**
 * MDA-1400
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
@Data
public class ResponseStatusListObject {

    @JsonProperty("ResponseStatusListObject")
    private ResponseStatusListObject.ResponseStatusObjects responseStatusObjects;

    @Data
    public static class ResponseStatusObjects {
        @JsonProperty("ResponseStatusObject")
        private List<ResponseStatus> responseStatusObject;
    }
}
