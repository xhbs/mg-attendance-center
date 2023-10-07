package com.unisinsight.business.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假、实习、申诉 等记录的公共字段
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/18
 * @since 1.0
 */
@Getter
@Setter
public class ApprovalRecord {
    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 流程审批状态
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 上报组织路径
     */
    @Column(name = "org_index_path")
    private String orgIndexPath;

    /**
     * 上报组织层级名称
     */
    @Column(name = "org_path_name")
    private String orgPathName;

    /**
     * 上报人编号
     */
    @Column(name = "creator_code")
    private String creatorCode;

    /**
     * 上报人姓名
     */
    @Column(name = "creator_name")
    private String creatorName;

    /**
     * 上报人角色名
     */
    @Column(name = "creator_role_name")
    private String creatorRoleName;

    /**
     * 开始日期
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 上报时间
     */
    @Column(name = "reported_at")
    private LocalDateTime reportedAt;
}
