package com.unisinsight.business.bo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QuerySubsidStuBO {

    /**
     *名单规则id
     */
    private Integer subsidRuleId;

    /**
     * 名单标识
     */
    private String subListIndex;


    /**
     * 组织标识
     */
    private String orgIndex;

    private List<String> orgIndexs;

    private List<String> orgParentIndexs;

    /**
     * 父组织标识
     */
    private String orgParentIndex;
    /**
     * 学生编号
     */
    private String personNo;

    /**
     * 学籍号
     */
    private String schoolRollNo;



}