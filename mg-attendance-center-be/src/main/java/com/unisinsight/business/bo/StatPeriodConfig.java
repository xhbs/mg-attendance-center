package com.unisinsight.business.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 在校统计周期配置
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/3/24
 * @since 1.0
 */
@Data
public class StatPeriodConfig {
    @ApiModelProperty(value = "开始日期", example = "1")
    @NotNull
    @Min(1)
    @Max(31)
    private Integer startDayOfMonth;

    @ApiModelProperty(value = "结束日期", example = "31")
    @NotNull
    @Min(1)
    @Max(31)
    private Integer endDayOfMonth;
}
