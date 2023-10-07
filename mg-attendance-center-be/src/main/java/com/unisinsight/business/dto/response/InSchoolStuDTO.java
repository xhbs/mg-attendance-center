package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/16
 */
@Data
@ApiModel(value = "在校学生查询")
public class InSchoolStuDTO {

    @ApiModelProperty(value = "人员编码")
    private String personNo;

    @ApiModelProperty(value = "人员名称")
    private String personName;

    @ApiModelProperty(value = "人员照片")
    private String personUrl;

    @ApiModelProperty(value = "班级组织名称")
    private String orgName;

    @ApiModelProperty(value = "考勤周期-开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "考勤周期-结束日期")
    private LocalDate endDate;

    @ApiModelProperty(value = "考勤明细(日期:状态)")
    private Map<String,Integer> attendDetails;

    @ApiModelProperty(value = "是否可以有感考勤")
    private Boolean canFeelAttend;
}
