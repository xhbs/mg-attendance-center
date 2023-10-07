package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 抽查考勤
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
@ApiModel(value = "有感考勤列表查询")
public class SpotCheckAttendDetailReqDTO extends PageParam {

    @ApiModelProperty(name = "task_Id", value = "任务id", required = true, example = "36")
    @NotNull
    private Integer taskId;

    @ApiModelProperty(name = "day", value = "有感考勤日期", required = true, example = "2021-09-05")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate day;

    @ApiModelProperty(name = "search_key", value = "搜索字段",example = "张三")
    private String searchKey;

    @ApiModelProperty(name = "status", value = "考勤状态(0-不限,1-已考勤,2-未考勤)", required = true)
    @NotNull
    private Integer status;

    @ApiModelProperty(name = "result", value = "考勤结果", example = "0")
    private Integer result;
}
