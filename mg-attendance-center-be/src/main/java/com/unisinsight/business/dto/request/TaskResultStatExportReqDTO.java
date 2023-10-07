package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 抽查考勤统计excel导出 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskResultStatExportReqDTO extends TaskResultStatReqDTO {

    @ApiModelProperty(value = "导出excel标题", required = true)
    @NotNull
    private String title;
}
