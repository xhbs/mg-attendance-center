package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 日常考勤排除日期 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@Data
public class DailyAttendanceExcludeDateDTO {

    /**
     * 主键
     */
    @ApiModelProperty(name = "id", value = "ID", required = true)
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty(name = "name", value = "名称", required = true)
    private String name;

    /**
     * 类型：1节假日 2自定义日期
     */
    @ApiModelProperty(name = "type", value = "类型：1节假日 2自定义日期", required = true)
    private Integer type;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_date", value = "开始日期，yyyy-MM-dd", required = true)
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(name = "end_date", value = "结束日期，yyyy-MM-dd", required = true)
    private LocalDate endDate;
}
