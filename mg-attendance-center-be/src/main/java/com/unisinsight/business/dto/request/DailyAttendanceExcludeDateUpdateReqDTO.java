package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 日常考勤排除日期修改 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DailyAttendanceExcludeDateUpdateReqDTO extends DailyAttendanceExcludeDateAddReqDTO {
    @ApiModelProperty(name = "id", value = "id", required = true)
    @NotNull
    private Integer id;
}
