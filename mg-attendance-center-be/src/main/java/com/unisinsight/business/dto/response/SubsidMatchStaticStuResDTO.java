package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
@Data
@ApiModel("资助比对学生列表")
public class SubsidMatchStaticStuResDTO {

    @ApiModelProperty(value = "记录id")
    private Long id;

    @ApiModelProperty(value = "学生编号")
    private String personNo;

    @ApiModelProperty(value = "比对状态,0-未通过，1-通过")
    private String status;

    @ApiModelProperty(value = "学生姓名")
    private String personName;

    @ApiModelProperty(value = "学生照片")
    private String personPic;

    @ApiModelProperty(value = "组织标识")
    private String orgIndex;

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    @ApiModelProperty(value = "父组织标识")
    private String orgParentIndex;

    @ApiModelProperty(value = "父组织名称")
    private String orgParentName;

    @ApiModelProperty(value = "正常次数")
    private Integer normalNum;

    @ApiModelProperty(value = "缺勤次数")
    private Integer absentNum;

    @ApiModelProperty(value = "资助名单规则id")
    private Long subsidRuleId;

    @ApiModelProperty(value = "缺勤率")
    private Integer absentRate;

    @ApiModelProperty(value = "考勤开始时间")
    private LocalDate chkDateSt;

    @ApiModelProperty(value = "考勤结束时间")
    private LocalDate chkDateEd;



}