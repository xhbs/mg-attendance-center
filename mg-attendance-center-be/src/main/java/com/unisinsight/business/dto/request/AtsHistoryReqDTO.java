package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 考勤历史 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/25
 * @since 1.0
 */
@Getter
@Setter
public class AtsHistoryReqDTO {
    @ApiModelProperty(name = "person_no", value = "人员编号", required = true)
    @NotNull
    @ApiParam("person_no")
    private String personNo;

    @ApiModelProperty(name = "start_time", value = "开始时间，13位时间戳", required = true)
    @NotNull
    @ApiParam("start_time")
    private Long startTime;

    @ApiModelProperty(name = "end_time", value = "结束时间，13位时间戳", required = true)
    @NotNull
    @ApiParam("end_time")
    private Long endTime;
}
