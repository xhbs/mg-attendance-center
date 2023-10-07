package com.unisinsight.business.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "subsid_match_static_stu")
@Data
public class SubsidMatchStaticStuDO {
    @Id
    private Long id;

    /**
     * 学生编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 比对状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 组织标识
     */
    @Column(name = "org_index")
    private String orgIndex;


    /**
     * 父组织标识
     */
    @Column(name = "org_parent_index")
    private String orgParentIndex;

    /**
     * 正常次数
     */
    @Column(name = "normal_num")
    private Integer normalNum;

    /**
     * 缺勤次数
     */
    @Column(name = "absent_num")
    private Integer absentNum;

    /**
     * 名单id
     */
    @Column(name = "subsid_rule_id")
    private Integer subsidRuleId;

    /**
     * 缺勤率
     */
    @Column(name = "absent_rate")
    private Integer absentRate;



    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;


}