package com.unisinsight.business.rpc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公共服务uuv获取用户详情（通过用户code获取)接口返回体
 */
@Data
public class UserDetailResDTO {

    /**
     * 性别，0代表男，1代表女
     */
    @ApiModelProperty("性别，0代表男，1代表女")
    private String gender;

    /**
     * 用户姓名
     */
    @ApiModelProperty("用户姓名")
    @JsonProperty("user_name")
    private String userName;

    /**
     * 手机
     */
    @ApiModelProperty("手机")
    @JsonProperty("cell_phone")
    private String cellPhone;

    @JsonProperty("user_type")
    private String userType;

    /**
     * 用户编号
     */
    @ApiModelProperty("用户编号")
    @JsonProperty("user_code")
    private String userCode;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @JsonProperty("user_id")
    private int userId;

    /**
     * 组织id
     */
    @ApiModelProperty("组织id")
    @JsonProperty("org_id")
    private int orgId;

    @ApiModelProperty("组织编码")
    @JsonProperty("org_code")
    private String orgCode;

    /**
     * 身份证
     */
    @ApiModelProperty("身份证")
    @JsonProperty("identity_no")
    private String identityNo;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 状态，0为启用，1为禁用
     */
    @ApiModelProperty("状态，0为启用，1为禁用")
    private int status;

    /**
     * 组织名
     */
    @ApiModelProperty("组织名")
    @JsonProperty("org_name")
    private String orgName;

    /**
     * 组织编号
     */
    @JsonProperty("org_index")
    private String orgIndex;
}
