package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 实习人员
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@Data
public class PersonBaseInfoDTO {

    /**
     * 人员编号
     */
    @ApiModelProperty(value = "人员编号")
    private String personNo;

    /**
     * 人员姓名
     */
    @ApiModelProperty(value = "人员姓名")
    private String personName;

    /**
     * 人员照片
     */
    @ApiModelProperty(value = "人员照片")
    private String personUrl;

    /**
     * 人员分组编号
     */
    @ApiModelProperty(name = "department_code", value = "人员分组编号")
    @JsonProperty("department_code")
    private String orgIndex;

    /**
     * 人员分组名称
     */
    @ApiModelProperty(name = "department_name", value = "人员分组名称")
    @JsonProperty("department_name")
    private String orgName;
}
