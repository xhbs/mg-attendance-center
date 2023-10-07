package com.unisinsight.business.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 申诉记录表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "appeal_records")
public class AppealRecordDO extends ApprovalRecord {

    /**
     * 申诉标题
     */
    @Column(name = "title")
    private String title;

    /**
     * 申诉内容
     */
    @Column(name = "content")
    private String content;

    /**
     * 学校父级组织类型
     */
    @Column(name = "school_parent_sub_type")
    private Short schoolParentSubType;
}