package com.unisinsight.business.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class TabListReq {
    @JSONField(name = "page_num")
    private Integer pageNum = 1;
    @JSONField(name = "page_size")
    private Integer pageSize = 1;
    @JSONField(name = "tab_name")
    private String tabName;

    public TabListReq(String tabName) {
        this.tabName = tabName;
    }
}
