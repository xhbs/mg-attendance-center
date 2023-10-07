package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "资助比对节点统计查询")
public class SubsidMatchStaticLevelReqDTO extends PageParam {

    @ApiModelProperty(value = "组织标识")
    private String orgIndex;

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    @ApiModelProperty(value = "父组织标识",required = true)
    private String orgParentIndex;

    @ApiModelProperty(value = "资助比对规则id",required = true)
    private Integer subsidRuleId;

    @ApiModelProperty(value = "缺勤率",required = true)
    private Integer absentRate;

    @ApiModelProperty(value = "考勤开始时间",required = true)
    private Date chkDateSt;

    @ApiModelProperty(value = "考勤结束时间",required = true)
    private Date chkDateEd;

    @ApiModelProperty(value = "组织类型")
    private Short subType;

    private Integer limit ;

    private Integer offset ;

}