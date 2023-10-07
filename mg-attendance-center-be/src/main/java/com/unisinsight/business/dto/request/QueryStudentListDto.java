package com.unisinsight.business.dto.request;

import com.unisinsight.business.dto.base.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询学生列表 入参
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryStudentListDto extends PaginationReq {

    @ApiModelProperty(value = "搜索字段")
    private String searchKey;

    @ApiModelProperty(value = "类型")
    private Integer type;

}
