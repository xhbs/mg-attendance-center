package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 日常考勤排除日期分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DailyAttendanceExcludeDateQueryReqDTO extends PaginationReq {

    @ApiModelProperty(name = "type", value = "类型：1节假日 2自定义日期")
    @Min(1)
    @Max(2)
    private Integer type;

    @ApiModelProperty(name = "name", value = "名称，模糊查询")
    private String name;
}
