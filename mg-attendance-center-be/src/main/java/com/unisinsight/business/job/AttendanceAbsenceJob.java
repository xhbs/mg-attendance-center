package com.unisinsight.business.job;

import java.time.LocalDate;

/**
 * 生成缺勤的定时任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/14
 */
public interface AttendanceAbsenceJob extends Runnable {

    /**
     * 生成某天的缺勤
     */
    void generate(LocalDate date);
}
