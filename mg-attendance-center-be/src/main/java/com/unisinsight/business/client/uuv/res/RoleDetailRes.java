package com.unisinsight.business.client.uuv.res;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName : mg-attendance-center
 * @Description :
 * @Author : xiehb
 * @Date: 2022/11/07 17:51
 * @Version 1.0.0
 */
@NoArgsConstructor
@Data
public class RoleDetailRes {

    @JSONField(name = "role_id")
    private Integer roleId;
    @JSONField(name = "role_code")
    private String roleCode;
    @JSONField(name = "role_name")
    private String roleName;
    @JSONField(name = "description")
    private Object description;
    @JSONField(name = "active_start_date")
    private Object activeStartDate;
    @JSONField(name = "active_start_time")
    private Object activeStartTime;
    @JSONField(name = "active_end_date")
    private Object activeEndDate;
    @JSONField(name = "active_end_time")
    private Object activeEndTime;
    @JSONField(name = "role_type")
    private Integer roleType;
    @JSONField(name = "create_time")
    private String createTime;
    @JSONField(name = "create_user")
    private Object createUser;
    @JSONField(name = "update_user")
    private Object updateUser;
    @JSONField(name = "update_time")
    private Object updateTime;
    @JSONField(name = "is_expired")
    private Integer isExpired;
}
