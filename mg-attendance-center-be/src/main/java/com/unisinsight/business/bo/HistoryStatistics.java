package com.unisinsight.business.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 考勤类型分布统计
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/25
 * @since 1.0
 */
@Getter
@Setter
public class HistoryStatistics {
    /**
     * 考勤状态类型
     */
    @ApiModelProperty(name = "type", value = "考勤状态类型:  0正常 1误报 2实 3请假 99 缺勤", example = "10")
    @JsonProperty("type")
    private Integer result;

    /**
     * 考勤次数
     */
    @ApiModelProperty(value = "考勤次数", example = "10")
    private int count;

    /**
     * 百分比
     */
    @ApiModelProperty(value = "百分比", example = "75.6")
    private float percent;
}
