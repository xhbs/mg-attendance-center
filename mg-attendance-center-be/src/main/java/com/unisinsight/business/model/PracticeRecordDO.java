package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 实习记录表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Data
@Table(name = "practice_records")
public class PracticeRecordDO extends ApprovalRecord {

    /**
     * 实习单位
     */
    @Column(name = "practice_company")
    private String practiceCompany;

    /**
     * 单位联系人
     */
    @Column(name = "company_contacts")
    private String companyContacts;

    /**
     * 单位联系电话
     */
    @Column(name = "contacts_phone")
    private String contactsPhone;

    /**
     * 实习状态 {@link com.unisinsight.business.common.enums.PracticeStatus}
     */
    @Column(name = "practice_status")
    private Short practiceStatus;

    /**
     * 实习点名状态
     */
    @Column(name = "attendance_state")
    private Integer attendanceState;
}
