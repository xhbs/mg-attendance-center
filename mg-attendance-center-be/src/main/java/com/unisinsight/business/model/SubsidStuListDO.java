package com.unisinsight.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "subsid_stu_list")
@Data
public class SubsidStuListDO {
    @Id
    private Integer id;

    /**
     * 名单标识(根据 名单年月(yyyy-mm)+项目类型（0-免学费，1-国家助学金）+数据类型（0-学校审核名单，1-主管审核名单）拼接)
     */
    @Column(name = "sub_list_index")
    private String subListIndex;

    /**
     * 学生编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 学生名称
     */
    @Column(name = "person_name")
    private String personName;

    /**
     * 学号
     */
    @Column(name = "student_no")
    private String studentNo;

    /**
     * 学籍号
     */
    @Column(name = "school_roll_no")
    private String schoolRollNo;

    /**
     * 证件类型
     */
    @Column(name = "cert_type")
    private String certType;

    /**
     * 证件号码
     */
    @Column(name = "cert_no")
    private String certNo;

    /**
     * 性别（0-女，1-男）
     */
    private String gender;

    /**
     * 出生日期
     */
    @Column(name = "birth_day")
    private Date birthDay;

    /**
     * 年级名称
     */
    @Column(name = "grade_name")
    private String gradeName;

    /**
     * 班级名称
     */
    @Column(name = "class_name")
    private String className;

    /**
     * 资助名单年份
     */
    private String year;

    /**
     * 资助名单月份
     */
    private String month;

    /**
     * 资助档次
     */
    @Column(name = "subsid_grade")
    private String subsidGrade;

    /**
     * 申请理由
     */
    @Column(name = "aply_reason")
    private String aplyReason;

    /**
     * 申请理由描述
     */
    @Column(name = "aply_reson_desc")
    private String aplyResonDesc;

    /**
     * 专业
     */
    private String major;

    /**
     * 入学日期
     */
    @Column(name = "admission_date")
    private String admissionDate;

    /**
     * 应发金额
     */
    @Column(name = "pay_amt")
    private String payAmt;

    /**
     * 银行卡号
     */
    @Column(name = "bank_no")
    private String bankNo;

    /**
     * 是否名族地区入学（0-否，1-是）
     */
    @Column(name = "nation_area_entry")
    private String nationAreaEntry;

    /**
     * 是否戏曲专业（0-否，1-是）
     */
    @Column(name = "tradition_opera_major")
    private String traditionOperaMajor;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

}