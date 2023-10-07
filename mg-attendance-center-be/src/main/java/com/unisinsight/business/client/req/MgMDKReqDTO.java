package com.unisinsight.business.client.req;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MgMDKReqDTO {
    private String name;
    private Integer type = 1;
    private Page page = new Page(1, 50);
    @JSONField(name = "tab_type")
    private String tabType = "05";

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Page {
        @JSONField(name = "page_num")
        private Integer pageNum;
        @JSONField(name = "page_size")
        private Integer pageSize;
    }
}
