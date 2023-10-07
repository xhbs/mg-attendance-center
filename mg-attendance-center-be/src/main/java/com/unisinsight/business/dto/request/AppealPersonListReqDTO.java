package com.unisinsight.business.dto.request;

import com.unisinsight.business.dto.base.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 申诉统计 - 查询班级下的申诉学生
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/10/8
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppealPersonListReqDTO extends PaginationReq {

    @ApiModelProperty(value = "组织编码")
    private String orgIndex;

    @ApiModelProperty(value = "审批状态： 1未上报 2审批中 3同意 4拒绝")
    private Integer status;

    @ApiModelProperty(value = "模糊搜索（姓名/学号）")
    private String search;
}
