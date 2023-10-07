package com.unisinsight.business.client.jinshan;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
public class TokenRes {

    @JSONField(name = "code")
    private Integer code;
    @JSONField(name = "msg")
    private String msg;
    @JSONField(name = "data")
    private DataDTO data;


    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JSONField(name = "token")
        private List<String> token;
    }


    public String getAccessToken() {
        if (CollUtil.isNotEmpty(data.getToken())) {
            Optional<String> accessToken = data.getToken().stream()
                    .filter(e -> e.contains("access_token"))
                    .findFirst();
            if (accessToken.isPresent()) {
                return accessToken.get().split(":")[1];
            }
        }
        return null;
    }

    public String getCompanyToken() {
        if (CollUtil.isNotEmpty(data.getToken())) {
            Optional<String> accessToken = data.getToken().stream()
                    .filter(e -> e.contains("company_token"))
                    .findFirst();
            if (accessToken.isPresent()) {
                return accessToken.get().split(":")[1];
            }
        }
        return null;
    }

//    {
//        "code": 200,
//            "msg": "操作成功",
//            "data": {
//        "token": [
//        "company_token:baa24491c46fa125805fcd2edcd29fcb",
//                "access_token:0897d47c4a1fae16d8960958fc06a76c"
//        ]
//    }
//    }
}
