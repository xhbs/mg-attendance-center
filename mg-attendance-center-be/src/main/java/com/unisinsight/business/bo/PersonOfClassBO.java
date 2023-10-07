package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 考勤人员信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Data
public class PersonOfClassBO {
    /**
     * 人员编号
     */
    private String personNo;

    /**
     * 人员名称
     */
    private String personName;

    /**
     * 组织编码（班级组织编码）
     */
    private String orgIndex;

    /**
     * 组织名称（班级组织名称）
     */
    private String orgName;
}