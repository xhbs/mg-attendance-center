package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 抽查考勤统计
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/6
 */
@Data
public class SpotTaskResultCountBO extends TaskResultCountBO {


    /**
     * 缺勤学生数
     */
    private int numOfAbsent;

    /**
     * 未点名学生数
     */
    private int numOfNull;

    /**
     * 总学生数
     */
    private int numOfTotal;

    /**
     * 已点名学生数
     */
    private int numOfFinish;

}
