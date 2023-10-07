package com.unisinsight.business.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class FindMdkRes {
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
            @JSONField(name = "total")
            private Integer total;
            @JSONField(name = "total_num")
            private Integer totalNum;
            @JSONField(name = "page_num")
            private Integer pageNum;
            @JSONField(name = "page_size")
            private Integer pageSize;
        }

        @NoArgsConstructor
        @Data
        public static class DataDTO1 {
            @JSONField(name = "image_url")
            private String imageUrl;
            @JSONField(name = "name")
            private String name;
            @JSONField(name = "gender")
            private Integer gender;
            @JSONField(name = "age")
            private Integer age;
            @JSONField(name = "certificate_type")
            private String certificateType;
            @JSONField(name = "certificate_id")
            private String certificateId;
            @JSONField(name = "birthday")
            private String birthday;
            @JSONField(name = "id_number")
            private String idNumber;
            @JSONField(name = "face_id")
            private String faceId;
        }
    }
}
