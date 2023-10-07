package com.unisinsight.business.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.unisinsight.framework.common.dto.ResultDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class InsertMdkRes {

    @JSONField(name = "error_code")
    private String errorCode;
    @JSONField(name = "message")
    private String message;
    @JSONField(name = "data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JSONField(name = "tab_id")
        private String tabId;
        @JSONField(name = "success")
        private List<ResultDTO> success;
        @JSONField(name = "fail")
        private List<ResultDTO> fail;

        @NoArgsConstructor
        @Data
        public static class ResultDTO {
            @JSONField(name = "face_id")
            private String faceId;
            @JSONField(name = "image_url")
            private String imageUrl;
            @JSONField(name = "file_name")
            private String fileName;
        }
    }
}
