package com.unisinsight.business.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实习点名结果
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/15
 */
@Data
public class PracticeAttendanceResultBO {

    /**
     * 实习人员ID
     */
    private Integer practicePersonId;

    /**
     * 人员编号
     */
    private Integer attendanceResult;

    /**
     * 考勤时间
     */
    private LocalDateTime attendanceTime;
}
