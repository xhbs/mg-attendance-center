package com.unisinsight.business.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class InsertMdkReq {

    @JSONField(name = "face_list")
    private List<FaceListDTO> faceList;
    @JSONField(name = "tab_i_d")
    private String tabID;

    @NoArgsConstructor
    @Data
    public static class FaceListDTO {
        @JSONField(name = "image")
        private String image;
        @JSONField(name = "name")
        private String name;
        @JSONField(name = "gender")
        private Integer gender;
        @JSONField(name = "nation")
        private String nation;
        @JSONField(name = "city")
        private String city;
        @JSONField(name = "person_address")
        private String personAddress;
        @JSONField(name = "person_code")
        private String personCode;
        @JSONField(name = "certificate_type")
        private String certificateType;
        @JSONField(name = "certificate_id")
        private String certificateId;
        @JSONField(name = "birthday")
        private Long birthday;
        @JSONField(name = "describ")
        private String describ;
        @JSONField(name = "file_name")
        private String fileName;
    }
}
