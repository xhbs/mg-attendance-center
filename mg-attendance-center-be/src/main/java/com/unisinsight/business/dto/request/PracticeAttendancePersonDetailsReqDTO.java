package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 实习点名详情查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@Data
public class PracticeAttendancePersonDetailsReqDTO {

    @ApiModelProperty(value = "实习申请记录ID", required = true)
    @NotNull
    private Integer recordId;

    @ApiModelProperty(value = "学生编号", required = true)
    @NotNull
    private String personNo;
}
