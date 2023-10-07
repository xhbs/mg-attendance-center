package com.unisinsight.business.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class FindMdkReq {
    @JSONField(name = "page_num")
    private Integer pageNum = 1;
    @JSONField(name = "page_size")
    private Integer pageSize = 5;
    @JSONField(name = "tab_id")
    private String tabId;
    @JSONField(name = "repo_type")
    private Integer repoType = 1;
    @JSONField(name = "id_number")
    private String idNumber;
}
