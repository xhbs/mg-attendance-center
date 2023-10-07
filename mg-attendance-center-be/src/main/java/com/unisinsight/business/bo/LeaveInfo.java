package com.unisinsight.business.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 核实 请假详情
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
@Data
public class LeaveInfo {

    /**
     * 请假状态
     */
    @ApiModelProperty(value = "请假状态; 1：未上报，2：审批中，3：同意，4：拒绝")
    private Short status;

    /**
     * 请假类型
     */
    @ApiModelProperty(value = "请假类型; 1：病假，2：事假，3：实习登记，99：其他")
    private Short type;

    /**
     * 请假开始时间
     */
    @ApiModelProperty(name = "start_time", value = "请假开始时间")
    @JsonProperty("start_time")
    private LocalDate startDate;

    /**
     * 请假结束时间
     */
    @ApiModelProperty(name = "end_time", value = "请假结束时间")
    @JsonProperty("end_time")
    private LocalDate endDate;

    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因")
    private String reason;
}
