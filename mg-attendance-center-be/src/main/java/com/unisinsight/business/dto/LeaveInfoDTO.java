package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 请假信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@Data
public class LeaveInfoDTO {

    /**
     * 请假类型
     */
    @ApiModelProperty(name = "type", value = "请假类型; 1：病假，2：事假，99：其他", required = true, example = "1")
    private Short type;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_date", value = "开始日期，yyyy-MM-dd", required = true, example = "2020-08-12")
    private String startDate;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "end_date", value = "结束日期，yyyy-MM-dd", required = true, example = "2020-08-12")
    private String endDate;

    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因")
    private String reason;
}
