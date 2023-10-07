package com.unisinsight.business.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 考勤人员信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Data
@Table(name = "persons")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDO {

    /**
     * 人员编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 人员名称
     */
    @Column(name = "person_name")
    private String personName;

    /**
     * 人员图像
     */
    @Column(name = "person_url")
    private String personUrl;

    /**
     * 性别
     */
    @Column(name = "gender")
    private String gender;

    /**
     * 入学日期
     */
    @Column(name = "admission_date")
    private LocalDate admissionDate;

    /**
     * 组织编码（班级组织名称）
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 组织名称（班级名称）
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 是否在籍
     */
    @Column(name = "registered")
    private Boolean registered;

    /**
     * 是否在校
     */
    @Column(name = "at_school")
    private Boolean atSchool;
}