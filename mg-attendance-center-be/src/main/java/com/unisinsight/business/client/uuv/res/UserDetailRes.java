package com.unisinsight.business.client.uuv.res;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName : mg-attendance-center
 * @Description :
 * @Author : xiehb
 * @Date: 2022/11/07 17:50
 * @Version 1.0.0
 */
@NoArgsConstructor
@Data
public class UserDetailRes {

    @JSONField(name = "birthday")
    private Object birthday;
    @JSONField(name = "address")
    private Object address;
    @JSONField(name = "gender")
    private String gender;
    @JSONField(name = "user_image")
    private String userImage;
    @JSONField(name = "is_reuse")
    private String isReuse;
    @JSONField(name = "user_name")
    private String userName;
    @JSONField(name = "work_phone")
    private Object workPhone;
    @JSONField(name = "is_thirdparty")
    private String isThirdparty;
    @JSONField(name = "ip_address")
    private Object ipAddress;
    @JSONField(name = "priority")
    private String priority;
    @JSONField(name = "cell_phone")
    private String cellPhone;
    @JSONField(name = "user_type")
    private String userType;
    @JSONField(name = "user_code")
    private String userCode;
    @JSONField(name = "user_id")
    private Integer userId;
    @JSONField(name = "org_id")
    private Integer orgId;
    @JSONField(name = "theme")
    private Object theme;
    @JSONField(name = "identity_no")
    private Object identityNo;
    @JSONField(name = "expiry_time")
    private Object expiryTime;
    @JSONField(name = "email")
    private Object email;
    @JSONField(name = "status")
    private Integer status;
    @JSONField(name = "org_name")
    private String orgName;
    @JSONField(name = "org_index")
    private String orgIndex;
    @JSONField(name = "roles")
    private List<Integer> roles;
}
