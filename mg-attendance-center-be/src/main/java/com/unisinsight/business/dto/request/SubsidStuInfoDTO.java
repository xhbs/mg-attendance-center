/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.dto.request;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:04:50
 * @since 1.0
 */
@Data
public class SubsidStuInfoDTO implements Serializable {

    /**
     * 名单标识(根据 名单年月(yyyy-mm)+项目类型（0-免学费，1-国家助学金）+数据类型（0-学校审核名单，1-主管审核名单）拼接)
     */
    private String subListIndex;

    /**
     * 学生编号
     */
    private String personNo;

    /**
     * 学生名称
     */
    private String personName;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 学籍号
     */
    private String schoolRollNo;

    /**
     * 证件类型
     */
    private String certType;

    /**
     * 证件号码
     */
    private String certNo;

    /**
     * 性别（0-女，1-男）
     */
    private String gender;

    /**
     * 出生日期
     */
    private Date birthDay;

    /**
     * 年级名称
     */
    private String gradeName;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 资助名单年份
     */
    private Short year;

    /**
     * 资助名单月份
     */
    private Short month;

    /**
     * 资助档次
     */
    private String subsidGrade;

    /**
     * 申请理由
     */
    private String aplyReason;

    /**
     * 申请理由描述
     */
    private String aplyResonDesc;

    /**
     * 专业
     */
    private String major;

    /**
     * 入学日期
     */
    private String admissionDate;

    /**
     * 应发金额
     */
    private BigDecimal payAmt;

    /**
     * 银行卡号
     */
    private Integer bankNo;

    /**
     * 是否名族地区入学（0-否，1-是）
     */
    private String nationAreaEntry;

    /**
     * 是否戏曲专业（0-否，1-是）
     */
    private String traditionOperaMajor;
}
