package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 申诉记录 列表查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppealRecordListDTO extends ApprovalRecordDTO {
    /**
     * 申诉标题
     */
    @ApiModelProperty(value = "申诉标题")
    private String title;

    /**
     * 申诉内容
     */
    @ApiModelProperty(value = "申诉内容")
    private String content;

    /**
     * 上报组织路径
     */
    @ApiModelProperty(value = "上报组织路径")
    private String orgIndexPath;

    /**
     * 上报组织层级名称
     */
    @ApiModelProperty(value = "上报组织层级名称")
    private String orgPathName;

    /**
     * 学校所属组织类型
     */
    @ApiModelProperty(value = "学校上级组织类型，1省 2市 3区")
    private Short schoolParentSubType;
}