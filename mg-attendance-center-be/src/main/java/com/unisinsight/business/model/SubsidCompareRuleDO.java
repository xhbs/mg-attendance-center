package com.unisinsight.business.model;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Date;

@Table(name = "subsid_compare_rule")
public class SubsidCompareRuleDO {
    @Id
    private Integer id;

    /**
     * 名单标识
     */
    @Column(name = "sub_list_index")
    private String subListIndex;

    /**
     * 缺勤率
     */
    @Column(name = "absent_rate")
    private Integer absentRate;

    /**
     * 考勤开始时间
     */
    @Column(name = "chk_date_st")
    private LocalDate chkDateSt;

    /**
     * 考勤结束时间
     */
    @Column(name = "chk_date_ed")
    private LocalDate chkDateEd;

    /**
     * 组织标识
     */
    @Column(name = "org_index")
    private String orgIndex;
    /**
     * 资助比对类型（0-手动，1-自动）
     */
    @Column(name = "subsid_type")
    private Short subsidType;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDate createTime;

    @Column(name = "school_year")
    private String schoolYear;

    @Column(name = "school_term")
    private String schoolTerm;

    @Column(name = "call_the_roll_absent_rate")
    private Integer callTheRollAbsentRate;

    @Column(name = "rule")
    private Integer rule;


    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getSchoolTerm() {
        return schoolTerm;
    }

    public void setSchoolTerm(String schoolTerm) {
        this.schoolTerm = schoolTerm;
    }

    public Integer getCallTheRollAbsentRate() {
        return callTheRollAbsentRate;
    }

    public void setCallTheRollAbsentRate(Integer callTheRollAbsentRate) {
        this.callTheRollAbsentRate = callTheRollAbsentRate;
    }

    public Integer getRule() {
        return rule;
    }

    public void setRule(Integer rule) {
        this.rule = rule;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取名单标识
     *
     * @return sub_list_index - 名单标识
     */
    public String getSubListIndex() {
        return subListIndex;
    }

    /**
     * 设置名单标识
     *
     * @param subListIndex 名单标识
     */
    public void setSubListIndex(String subListIndex) {
        this.subListIndex = subListIndex;
    }

    /**
     * 获取缺勤率
     *
     * @return absent_rate - 缺勤率
     */
    public Integer getAbsentRate() {
        return absentRate;
    }

    /**
     * 设置缺勤率
     *
     * @param absentRate 缺勤率
     */
    public void setAbsentRate(Integer absentRate) {
        this.absentRate = absentRate;
    }

    /**
     * 获取考勤开始时间
     *
     * @return chk_date_st - 考勤开始时间
     */
    public LocalDate getChkDateSt() {
        return chkDateSt;
    }

    /**
     * 设置考勤开始时间
     *
     * @param chkDateSt 考勤开始时间
     */
    public void setChkDateSt(LocalDate chkDateSt) {
        this.chkDateSt = chkDateSt;
    }

    /**
     * 获取考勤结束时间
     *
     * @return chk_date_ed - 考勤结束时间
     */
    public LocalDate getChkDateEd() {
        return chkDateEd;
    }

    /**
     * 设置考勤结束时间
     *
     * @param chkDateEd 考勤结束时间
     */
    public void setChkDateEd(LocalDate chkDateEd) {
        this.chkDateEd = chkDateEd;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public LocalDate getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDate createTime) {
        this.createTime = createTime;
    }

    public String getOrgIndex() {
        return orgIndex;
    }

    public void setOrgIndex(String orgIndex) {
        this.orgIndex = orgIndex;
    }

    public Short getSubsidType() {
        return subsidType;
    }

    public void setSubsidType(Short subsidType) {
        this.subsidType = subsidType;
    }
}