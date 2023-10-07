package com.unisinsight.business.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class TabListRes {
    @JSONField(name = "error_code")
    private String errorCode;
    @JSONField(name = "data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JSONField(name = "paging")
        private DataDTO.PagingDTO paging;
        @JSONField(name = "data")
        private List<DataDTO.DataDTO1> data;

        @NoArgsConstructor
        @Data
        public static class PagingDTO {
            @JSONField(name = "page_num")
            private Integer pageNum;
            @JSONField(name = "page_size")
            private Integer pageSize;
            @JSONField(name = "total_num")
            private Integer totalNum;
            @JSONField(name = "total_page")
            private Integer totalPage;
        }

        @NoArgsConstructor
        @Data
        public static class DataDTO1 {
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
}
