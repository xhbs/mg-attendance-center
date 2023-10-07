package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 实习点名详情查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PracticeAttendanceDetailsReqDTO extends PaginationReq {

    @ApiModelProperty(value = "实习申请记录ID", required = true)
    @NotNull
    private Integer recordId;

    @ApiModelProperty("考勤状态, -1暂未考勤 0考勤正常 99 考勤缺勤")
    private Integer attendanceResult;

    @ApiModelProperty("模糊搜索，学生姓名/学号/班级")
    private String search;
}
