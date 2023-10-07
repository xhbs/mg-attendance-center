package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 学校下学生
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/17
 */
@Data
public class PersonOfSchoolBO {

    /**
     * 学生编号
     */
    private String personNo;

    /**
     * 所在学校
     */
    private String orgIndexOfSchool;

    /**
     * 上次打卡时间
     */
    private long latestEventTime;
}
