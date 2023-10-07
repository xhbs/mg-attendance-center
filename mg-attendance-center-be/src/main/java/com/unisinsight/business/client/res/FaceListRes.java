package com.unisinsight.business.client.res;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class FaceListRes {

    @JSONField(name = "error_code")
    private String errorCode;
    @JSONField(name = "message")
    private String message;
    @JSONField(name = "data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JSONField(name = "count")
        private Integer count;
        @JSONField(name = "data")
        private List<DataDTO1> data;

        @NoArgsConstructor
        @Data
        public static class DataDTO1 {
            @JSONField(name = "record_id")
            private Long recordId;
            @JSONField(name = "face_id")
            private String faceId;
            @JSONField(name = "id_number")
            private String idNumber;
            @JSONField(name = "tab_name")
            private String tabName;
            private Double similarity;
            private String rid;
            private String name;

        }
    }
}
