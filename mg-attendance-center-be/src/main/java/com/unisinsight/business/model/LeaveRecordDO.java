package com.unisinsight.business.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 请假记录表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "leave_records")
public class LeaveRecordDO extends ApprovalRecord {
    /**
     * 请假类型
     */
    @Column(name = "type")
    private Short type;

    /**
     * 请假原因
     */
    @Column(name = "reason")
    private String reason;

    /**
     * 人员编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 人员姓名
     */
    @Column(name = "person_name")
    private String personName;

    /**
     * 组织编码
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 组织名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 请假状态
     */
    @Column(name = "leave_state")
    private Integer leaveState;

    /**
     * 学校名称
     */
    @Column(name = "school_name")
    private String schoolName;

    /**
     * 审核人姓名
     */
    @Column(name = "approved_by")
    private String approvedBy;
}
