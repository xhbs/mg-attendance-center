package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Data
@ApiModel("日常考勤统计-分页查询-入参")
public class DailyAttendanceHighLvlStaticQueryReqDTO extends PaginationReq {

    @ApiModelProperty(value = "学年(格式：2020-2021)")
    private String schoolYear;

    @ApiModelProperty(value = "学期（0-春季，1-秋季）")
    private String schoolTerm;

    @ApiModelProperty(value = "月份（yyyy-mm格式）")
    private String yearMonth;

    @ApiModelProperty(value = "组织节点")
    private String orgIndex;

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    @ApiModelProperty(value = "父组织节点")
    private String orgParentIndex;

    private Integer limit ;

    private Integer offset ;

}
