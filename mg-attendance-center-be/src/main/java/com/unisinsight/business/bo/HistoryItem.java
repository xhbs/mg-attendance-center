package com.unisinsight.business.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 考勤历史
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/25
 * @since 1.0
 */
@Data
public class HistoryItem {

    /**
     * 考勤状态类型  {@link com.unisinsight.business.common.enums.AttendanceResult}
     */
    @ApiModelProperty(name = "type", value = "考勤状态类型名称", example = "实习")
    @JsonProperty("type")
    private Integer result;

    /**
     * 考勤状态名称
     */
    @ApiModelProperty(name = "result", value = "考勤状态类型名称", example = "实习")
    @JsonProperty("result")
    private String resultName;

    /**
     * 考勤日期
     */
    @ApiModelProperty(value = "考勤日期，yyyy-MM-dd", example = "2020-12-25")
    private String date;
}
