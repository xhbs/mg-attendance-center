package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class SubsidMatchStaticLevelResDTO {

    @ApiModelProperty(value = "记录id")
    private Long id;

    @ApiModelProperty(value = "组织标识")
    private String orgIndex;

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    @ApiModelProperty(value = "父组织标识")
    private String orgParentIndex;

    @ApiModelProperty(value = "学生数量")
    private Integer studentNum;

    @ApiModelProperty(value = "资助学生总数")
    private Integer subNum;

    @ApiModelProperty(value = "比对通过人数")
    private Integer matchPassNum;

    @ApiModelProperty(value = "比对不通过人数")
    private Integer matchNoPassNum;

    @ApiModelProperty(value = "考勤开始时间")
    private LocalDate chkDateSt;

    @ApiModelProperty(value = "考勤结束时间")
    private LocalDate chkDateEd;

    @ApiModelProperty(value = "组织类型")
    private Short subType;




}