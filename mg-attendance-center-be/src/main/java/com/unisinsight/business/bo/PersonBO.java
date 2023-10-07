package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 考勤人员信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Data
public class PersonBO {
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
     * 组织索引（加上人员所在组织本级）
     */
    private String orgIndexPath;
}