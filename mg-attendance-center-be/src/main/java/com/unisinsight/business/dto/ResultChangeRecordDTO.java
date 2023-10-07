package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 考勤状态变更记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/16
 */
@Data
public class ResultChangeRecordDTO {
    @ApiModelProperty(name = "result", value = "考勤结果: 0正常 1误报 2实 3请假 99缺勤", required = true)
    private Integer result;

    @ApiModelProperty(name = "mode", value = "类型: 0原始结果  1系统变更 2手动变更", required = true)
    private Integer mode;

    @ApiModelProperty(name = "changed_at", value = "变更时间, yyyy-MM-dd HH:mm:ss", required = true)
    private String changedAt;

    @ApiModelProperty(name = "comment", value = "备注")
    private String comment;
}
