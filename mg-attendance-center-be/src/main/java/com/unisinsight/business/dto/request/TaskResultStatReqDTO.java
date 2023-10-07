package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 抽查考勤统计入参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskResultStatReqDTO extends PaginationReq {

    @ApiModelProperty(name = "task_id", value = "任务id", required = true, example = "1")
    @NotNull
    private Integer taskId;

    @ApiModelProperty(name = "attendance_date", value = "考勤日期, yyyy-MM-dd", example = "2021-08-25")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @ApiModelProperty(name = "org_index_paths", value = "组织索引集合")
    private List<String> orgIndexPaths;

    @ApiModelProperty(name = "school_org_index", value = "学校组织编码，当前组织是校级时候必传")
    private String schoolOrgIndex;

    @ApiModelProperty(name = "school_name", value = "学校名字")
    private String schoolName;
}
