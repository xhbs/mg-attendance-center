package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 申诉记录和人员关联表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@Data
@Table(name = "appeal_persons")
public class AppealPersonDO {
    /**
     * 申诉记录ID
     */
    @Column(name = "appeal_record_id")
    private Integer appealRecordId;

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
}