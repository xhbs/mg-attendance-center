package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 抽查考勤任务创建 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@Data
public class SpotCheckTaskAddReqDTO {

    @ApiModelProperty(name = "name", value = "任务名称", required = true, example = "8月抽查考勤")
    @NotNull
    @NotEmpty
    private String name;

    @ApiModelProperty(name = "start_date", value = "开始日期，yyyy-MM-dd", required = true, example = "2021-08-25")
    @NotNull
    private LocalDate startDate;

    @ApiModelProperty(name = "end_date", value = "结束日期，yyyy-MM-dd", required = true, example = "2021-09-05")
    @NotNull
    private LocalDate endDate;

    @ApiModelProperty(name = "day_count", value = "抽查天数", required = true, example = "4")
    @NotNull
    @Min(1)
    private Integer dayCount;

    @ApiModelProperty(name = "target_org_indexes", value = "考勤目标的组织编码集合", required = true, example = "[530900,532300]")
    @NotNull
    @NotEmpty
    private List<String> targetOrgIndexes;

    @ApiModelProperty(name = "school_year", value = "学年", required = true, example = "2021-2022")
    @NotNull
    private String schoolYear;

    @ApiModelProperty(name = "semester", value = "学期，不限则不传： 春季、秋季", example = "秋季")
    private String semester;

    @ApiModelProperty(name = "month", value = "月份，不限则不传：yyyy-MM", example = "2021-01")
    private String month;

    @ApiModelProperty(name = "minimum_absence_rate", value = "缺勤率最小值", example = "50", required = true)
    @NotNull
    @Min(0)
    private Integer minimumAbsenceRate;

    @ApiModelProperty(name = "call_the_roll", value = "点名", example = "0")
    private Integer callTheRoll;
    private Integer callTheRollFirstTaskId;

    private Boolean auto = Boolean.FALSE;

    private String test;
}
