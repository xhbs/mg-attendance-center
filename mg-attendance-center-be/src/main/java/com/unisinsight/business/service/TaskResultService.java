package com.unisinsight.business.service;

import com.unisinsight.business.common.enums.AdjustModeEnum;
import com.unisinsight.business.common.enums.AttendanceResult;

import java.time.LocalDate;

/**
 * 抽查考勤结果服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/6
 */
public interface TaskResultService {

    /**
     * 调整考勤记录
     */
    void updateResults(String personNo, LocalDate startDate, LocalDate endDate, AttendanceResult result,
                       String comment, AdjustModeEnum mode);


    /**
     * 调整考勤记录，如果当天没有记录则生成记录
     */
    void updateOrCreateResult(String personNo, Integer taskId, LocalDate date, AttendanceResult result,
                              String comment, AdjustModeEnum mode);
}
