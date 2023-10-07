package com.unisinsight.business.dto.request;

import com.unisinsight.business.dto.base.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 申诉统计分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppealStatListReqDTO extends PaginationReq {

    @ApiModelProperty(value = "组织编码", required = true)
    @NotNull
    private String orgIndex;

    @ApiModelProperty(value = "开始日期, yyyy-MM-dd")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期, yyyy-MM-dd")
    private LocalDate endDate;

    @ApiModelProperty(value = "模糊搜索(组织名称)")
    private String search;

}
