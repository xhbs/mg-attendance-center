package com.unisinsight.business.client.req;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class FaceTabReq {
    @JSONField(name = "tab_ids")
    private List<String> tabIds;
    private String image;
    private Integer threshold = 80;
    @JSONField(name = "top_k")
    private Integer topK = 10;
    private Integer type = 1;
    private String versions = "05005010006";
}
