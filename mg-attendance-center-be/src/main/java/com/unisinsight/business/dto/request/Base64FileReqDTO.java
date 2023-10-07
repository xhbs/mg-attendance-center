package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Base64FileReqDTO {

    @JsonProperty("base64_code")
    private String base64Code;
}
