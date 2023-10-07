package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 实习记录和人员关联表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@Data
@Table(name = "practice_persons")
public class PracticePersonDO {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 实习记录ID
     */
    @Column(name = "practice_record_id")
    private Integer practiceRecordId;

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
     * 实习点名考勤结果
     */
    @Column(name = "attendance_result")
    private Integer attendanceResult;

    /**
     * 实习点名考勤时间
     */
    @Column(name = "attendance_time")
    private Integer attendanceTime;

    /**
     * 缺勤消息已读标记
     */
    @Column(name = "read")
    private Boolean read;
}