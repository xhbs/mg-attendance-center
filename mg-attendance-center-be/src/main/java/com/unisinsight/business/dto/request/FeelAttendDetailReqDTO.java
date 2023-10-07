package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 有感考勤详情
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
@ApiModel(value = "有感考勤详情")
public class FeelAttendDetailReqDTO {

    @ApiModelProperty(name = "task_Id", value = "任务id", required = true, example = "36")
    @NotNull
    private Integer taskId;

    @ApiModelProperty(name = "person_no ", value = "学生编号", required = true, example = "2E8DD1C078BD44E92964E2E53646B7E5")
    @NotNull
    @NotEmpty
    private String personNo ;

    @ApiModelProperty(name = "day", value = "有感考勤日期", required = true, example = "2021-09-05")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate day;

    @ApiModelProperty(name = "result", value = "考勤结果", required = true,example = "99")
    @NotNull
    private Integer result;
}
