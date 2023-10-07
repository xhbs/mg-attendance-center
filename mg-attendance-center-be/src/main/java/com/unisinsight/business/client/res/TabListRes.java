package com.unisinsight.business.client.res;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class TabListRes {
    @JSONField(name = "error_code")
    private String errorCode;
    private Data data;

    @lombok.Data
    public static class Data{
        private Integer total;
        private List<Repository> repositories;

        @lombok.Data
        public static class Repository{
            private String id;
            private String name;
            private String type;
            private Integer count;
            @JSONField(name = "factory_name")
            private String factoryName;
            @JSONField(name = "factory_code")
            private String factoryCode;
            private String origin;
        }
    }
}
