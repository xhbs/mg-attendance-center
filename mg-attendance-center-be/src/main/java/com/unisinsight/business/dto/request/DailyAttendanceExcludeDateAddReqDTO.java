package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 日常考勤排除日期添加 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@Data
public class DailyAttendanceExcludeDateAddReqDTO {

    /**
     * 名称
     */
    @ApiModelProperty(name = "name", value = "名称", required = true)
    @NotNull
    private String name;

    /**
     * 类型：1节假日 2自定义日期
     */
    @ApiModelProperty(name = "type", value = "类型：1节假日 2自定义日期", required = true)
    @NotNull
    @Min(1)
    @Max(2)
    private Integer type;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_date", value = "开始日期，yyyy-MM-dd", required = true)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(name = "end_date", value = "结束日期，yyyy-MM-dd", required = true)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
