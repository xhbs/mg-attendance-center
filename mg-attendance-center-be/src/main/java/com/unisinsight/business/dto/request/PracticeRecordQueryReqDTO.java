package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 实习申请 分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("实习申请-分页查询-入参")
public class PracticeRecordQueryReqDTO extends ApprovalRecordQueryReqDTO {
    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_time", value = "开始日期, yyyy-MM-dd")
    @JsonProperty("start_time")
    @ApiParam("start_time")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(name = "end_time", value = "结束日期, yyyy-MM-dd")
    @JsonProperty("end_time")
    @ApiParam("end_time")
    private LocalDate endDate;

    @ApiModelProperty(value = "实习点名状态")
    @JsonProperty("attendance_state")
    private Integer attendanceState;
}
