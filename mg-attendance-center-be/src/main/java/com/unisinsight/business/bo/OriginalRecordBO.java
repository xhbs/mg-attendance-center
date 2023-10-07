package com.unisinsight.business.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 原始记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OriginalRecordBO extends PersonBO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 抓拍时间
     */
    private LocalDateTime passTime;

    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;
}
