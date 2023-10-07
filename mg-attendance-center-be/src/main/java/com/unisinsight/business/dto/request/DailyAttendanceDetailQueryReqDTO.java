package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 日常考勤明细查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DailyAttendanceDetailQueryReqDTO extends PaginationReq {

    @ApiModelProperty(name = "school_year", value = "学年", required = true, example = "2021-2022")
    @NotNull
    private String schoolYear;

    @ApiModelProperty(name = "semester", value = "学期，春季、秋季", example = "秋季")
    private String semester;

    @ApiModelProperty(name = "month", value = "月份，yyyy-MM", example = "2021-01")
    private String month;

    @ApiModelProperty(name = "person_no", value = "学生编号", required = true, example = "person_1")
    @NotNull
    private String personNo;

    @ApiModelProperty(name = "result", value = "状态：0在校 99缺勤", example = "0")
    private Integer result;

    @ApiModelProperty(name = "order_by_result_desc", value = "状态倒序排序（缺勤在前面），默认true", example = "true")
    private Boolean orderByResultDesc;
}

