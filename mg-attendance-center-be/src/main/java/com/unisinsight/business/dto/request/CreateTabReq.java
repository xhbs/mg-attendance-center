package com.unisinsight.business.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateTabReq {
    @JSONField(name = "tab_name")
    private String tabName;
    @JSONField(name = "is_affirmed")
    private String isAffirmed;
    @JSONField(name = "repo_type")
    private Integer repoType;
    @JSONField(name = "tab_type")
    private String tabType;
    @JSONField(name = "creator")
    private String creator;
}
