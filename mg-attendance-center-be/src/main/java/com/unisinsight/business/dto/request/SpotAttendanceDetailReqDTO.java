package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 抽查考勤详情 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpotAttendanceDetailReqDTO extends DailyAttendanceDetailReqDTO {

    @ApiModelProperty(name = "task_id", value = "任务ID", required = true, example = "1")
    @NotNull
    private Integer taskId;
}
