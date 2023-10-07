package com.unisinsight.business.bo;

import lombok.Data;

import java.util.Date;

@Data
public class SubsidStuBO  {

    private Integer id;

    /**
     * 名单标识
     */
    private String subListIndex;

    /**
     * 组织标识
     */
    private String orgIndex;

    /**
     * 父组织标识
     */
    private String orgParentIndex;
    /**
     * 学生编号
     */
    private String personNo;

    /**
     * 学生名称
     */
    private String personName;

    /**
     * 学籍号
     */
    private String schoolRollNo;



}