package com.unisinsight.business.dto.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 根据新的Restful规范定义的通用分页参数
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/8/19
 * @since 1.0
 */
@Data
public class PaginationReq {

    @ApiModelProperty(name = "page_num", value = "页码", required = true, example = "1")
    @NotNull
    private Integer pageNum = 1;

    @ApiModelProperty(name = "page_size", value = "页容量", required = true, example = "10")
    @NotNull
    private Integer pageSize = 10;
}
