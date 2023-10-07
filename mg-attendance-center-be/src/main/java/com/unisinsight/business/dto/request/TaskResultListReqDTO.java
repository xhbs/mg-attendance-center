package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 抽查考勤明细查询 入参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskResultListReqDTO extends PaginationReq {

    @ApiModelProperty(name = "task_id", value = "任务ID", required = true, example = "1")
    @NotNull
    private Integer taskId;

    @ApiModelProperty(name = "school_org_index", value = "学校组织编码", example = "03EF34E0C0BA4D7CE0537D64A8C02609")
    @NotNull
    private String schoolOrgIndex;

    @ApiModelProperty(name = "attendance_date", value = "考勤日期, yyyy-MM-dd", example = "2021-08-25")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @ApiModelProperty(name = "result", value = "考勤结果", example = "0")
    private Integer result;

    @ApiModelProperty(name = "search_key", value = "搜索字段,学生姓名,学号,班级名", example = "大一1班")
    private String searchKey;
}
