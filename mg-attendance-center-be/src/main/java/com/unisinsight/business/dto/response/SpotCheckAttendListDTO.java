package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 抽查考勤明细查询 出参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
public class SpotCheckAttendListDTO {

    @ApiModelProperty(name = "task_id", value = "抽查考勤任务id", required = true, example = "1")
    private Integer taskId;

    @ApiModelProperty(name = "task_name", value = "抽查考勤任务名称", required = true, example = "张三")
    private String taskName;

    @ApiModelProperty(name = "start_date", value = "开始日期，yyyy-MM-dd", required = true, example = "2021-08-25")
    private LocalDate startDate;

    @ApiModelProperty(name = "end_date", value = "结束日期，yyyy-MM-dd", required = true, example = "2021-09-05")
    private LocalDate endDate;

    @ApiModelProperty(name = "spot_checked", value = "已抽查", required = true, example = "3")
    private Integer spotChecked;

    @ApiModelProperty(name = "day_count", value = "抽查总天数", required = true, example = "5")
    private Integer dayCount;

    @ApiModelProperty(name = "spot_checked_dates", value = "已抽查的日期列表", required = true)
    private List<String> spotCheckedDates;

    @ApiModelProperty(name = "can_feel_attend", value = "是否可以有感考勤", required = true, example = "true")
    private Boolean canFeelAttend;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String spotCheckedDateStr;
}
