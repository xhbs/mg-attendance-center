package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 抽查考勤任务更新入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpotCheckTaskUpdateReqDTO extends SpotCheckTaskAddReqDTO {

    @ApiModelProperty(name = "id", value = "任务ID", required = true)
    @NotNull
    private Integer id;
}
