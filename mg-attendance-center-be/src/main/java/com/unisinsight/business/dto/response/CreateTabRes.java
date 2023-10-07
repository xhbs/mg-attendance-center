package com.unisinsight.business.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateTabRes {
    @JSONField(name = "error_code")
    private String errorCode;
    @JSONField(name = "data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JSONField(name = "tab_id")
        private String tabId;
        @JSONField(name = "repo_type")
        private Integer repoType;
        @JSONField(name = "tab_name")
        private String tabName;
        @JSONField(name = "is_affirmed")
        private Integer isAffirmed;
        @JSONField(name = "tab_type")
        private String tabType;
        @JSONField(name = "create_time")
        private String createTime;
        @JSONField(name = "update_time")
        private String updateTime;
        @JSONField(name = "algorithm")
        private String algorithm;
        @JSONField(name = "creator")
        private String creator;
        @JSONField(name = "obj_num")
        private Integer objNum;
        @JSONField(name = "domain_id")
        private String domainId;
        @JSONField(name = "origin")
        private String origin;
    }
}
