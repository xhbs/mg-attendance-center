package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "subsid_match_static_level")
public class SubsidMatchStaticLevelDO {
    @Id
    private Long id;

    /**
     * 组织标识
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 组织名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 父组织标识
     */
    @Column(name = "org_parent_index")
    private String orgParentIndex;

    /**
     * 学生数量
     */
    @Column(name = "student_num")
    private int studentNum;

    /**
     * 资助学生总数
     */
    @Column(name = "sub_num")
    private int subNum;

    /**
     * 比对通过人数
     */
    @Column(name = "match_pass_num")
    private int matchPassNum;

    /**
     * 比对不通过人数
     */
    @Column(name = "match_no_pass_num")
    private int matchNoPassNum;

    /**
     * 名单id
     */
    @Column(name = "subsid_rule_id")
    private int subsidRuleId;

    /**
     * 组织类型
     */
    @Column(name = "sub_type")
    private Short subType;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

}