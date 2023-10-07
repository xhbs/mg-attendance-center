package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 日常考勤设置
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@Data
public class DailyAttendanceSettingDTO {

    /**
     * 是否启用
     */
    @ApiModelProperty(name = "enable", value = "是否启用", required = true)
    @NotNull
    private Boolean enable;

    /**
     * 是否排除排除周末
     */
    @ApiModelProperty(name = "exclude_weekends", value = "是否排除排除周末", required = true)
    @NotNull
    private Boolean excludeWeekends;

    /**
     * 是否排除节假日
     */
    @ApiModelProperty(name = "exclude_holidays", value = "是否排除节假日", required = true)
    @NotNull
    private Boolean excludeHolidays;

    /**
     * 是否排除自定义日期
     */
    @ApiModelProperty(name = "exclude_custom_dates", value = "是否排除自定义日期", required = true)
    @NotNull
    private Boolean excludeCustomDates;
}
