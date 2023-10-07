package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 实习点名分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PracticeAttendanceListReqDTO extends PaginationReq {

    @ApiModelProperty(value = "组织索引", required = true)
    @NotNull
    private String orgIndexPath;

    @ApiModelProperty("考勤状态, 0进行中 1通过 2未通过")
    private Integer attendanceState;

    @ApiModelProperty("申请时间范围最小值 yyyy-MM-dd")
    private LocalDate reportTimeMin;

    @ApiModelProperty("申请时间范围最大值 yyyy-MM-dd")
    private LocalDate reportTimeMax;

    @ApiModelProperty("模糊搜索，学生姓名")
    private String search;
}
