package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 抽查考勤统计
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/6
 */
@Data
public class TaskResultCountBO {

    /**
     * 在校学生数
     */
    private int numOfNormal;

    /**
     * 请假学生数
     */
    private int numOfLeave;

    /**
     * 实习学生数
     */
    private int numOfPractice;

    /**
     * 申诉通过学生数
     */
    private int numOfAppeal;
}
