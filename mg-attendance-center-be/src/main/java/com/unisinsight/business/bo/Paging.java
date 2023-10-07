package com.unisinsight.business.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 根据新的Restful规范定义的通用分页参数
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/8/19
 * @since 1.0
 */
@Data
@ApiModel("通用分页请求出参")
public class Paging {
    @ApiModelProperty(value = "页码", required = true, example = "1")
    private Integer pageNum;

    @ApiModelProperty(value = "页容量", required = true, example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "总数", required = true, example = "100")
    private Long total;
}
