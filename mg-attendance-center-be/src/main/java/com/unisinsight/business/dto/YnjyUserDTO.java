package com.unisinsight.business.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class YnjyUserDTO {
    @JSONField(name = "code")
    private Integer code;
    @JSONField(name = "msg")
    private String msg;
    @JSONField(name = "data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JSONField(name = "access_token")
        private String accessToken;
        @JSONField(name = "token_type")
        private String tokenType;
        @JSONField(name = "refresh_token")
        private String refreshToken;
        @JSONField(name = "expires_in")
        private Integer expiresIn;
        @JSONField(name = "scope")
        private String scope;
        @JSONField(name = "public_user")
        private PublicUserDTO publicUser;
        @JSONField(name = "active")
        private Boolean active;

        @NoArgsConstructor
        @Data
        public static class PublicUserDTO {
            @JSONField(name = "id")
            private Long id;
            @JSONField(name = "username")
            private String username;
            @JSONField(name = "password")
            private String password;
            @JSONField(name = "nickname")
            private String nickname;
            @JSONField(name = "mobile")
            private String mobile;
            @JSONField(name = "head")
            private String head;
            @JSONField(name = "email")
            private String email;
            @JSONField(name = "wechatId")
            private String wechatId;
            @JSONField(name = "wxOpenId")
            private String wxOpenId;
            @JSONField(name = "qqOpenId")
            private String qqOpenId;
            @JSONField(name = "wxmpOpenId")
            private String wxmpOpenId;
            @JSONField(name = "qq")
            private String qq;
            @JSONField(name = "description")
            private String description;
            @JSONField(name = "flag")
            private Integer flag;
            @JSONField(name = "status")
            private Integer status;
            @JSONField(name = "personal")
            private PersonalDTO personal;
            @JSONField(name = "personalTypes")
            private List<PersonalTypesDTO> personalTypes;
            @JSONField(name = "authorities")
            private List<AuthoritiesDTO> authorities;
            @JSONField(name = "resouce")
            private List<?> resouce;
            @JSONField(name = "enabled")
            private Boolean enabled;
            @JSONField(name = "credentialsNonExpired")
            private Boolean credentialsNonExpired;
            @JSONField(name = "accountNonLocked")
            private Boolean accountNonLocked;
            @JSONField(name = "accountNonExpired")
            private Boolean accountNonExpired;

            @NoArgsConstructor
            @Data
            public static class PersonalDTO {
                @JSONField(name = "id")
                private String id;
                @JSONField(name = "publicUserId")
                private String publicUserId;
                @JSONField(name = "name")
                private String name;
                @JSONField(name = "englishName")
                private String englishName;
                @JSONField(name = "namePinyin")
                private String namePinyin;
                @JSONField(name = "oldName")
                private String oldName;
                @JSONField(name = "nativePlace")
                private String nativePlace;
                @JSONField(name = "nation")
                private String nation;
                @JSONField(name = "politicalOutlook")
                private String politicalOutlook;
                @JSONField(name = "religiousBelief")
                private String religiousBelief;
                @JSONField(name = "healthStatus")
                private String healthStatus;
                @JSONField(name = "bloodType")
                private String bloodType;
                @JSONField(name = "sex")
                private Integer sex;
                @JSONField(name = "birthday")
                private Object birthday;
                @JSONField(name = "birthAddr")
                private String birthAddr;
                @JSONField(name = "nationality")
                private String nationality;
                @JSONField(name = "maritalStatus")
                private String maritalStatus;
                @JSONField(name = "icard")
                private String icard;
                @JSONField(name = "icardurl")
                private String icardurl;
                @JSONField(name = "residenceAddr")
                private String residenceAddr;
                @JSONField(name = "address")
                private String address;
                @JSONField(name = "mobile")
                private String mobile;
                @JSONField(name = "email")
                private String email;
                @JSONField(name = "postalCode")
                private Integer postalCode;
                @JSONField(name = "phone")
                private String phone;
                @JSONField(name = "status")
                private Integer status;
            }

            @NoArgsConstructor
            @Data
            public static class PersonalTypesDTO {
                @JSONField(name = "id")
                private Integer id;
                @JSONField(name = "code")
                private String code;
                @JSONField(name = "name")
                private String name;
            }

            @NoArgsConstructor
            @Data
            public static class AuthoritiesDTO {
                @JSONField(name = "authority")
                private String authority;
            }
        }
    }
}
