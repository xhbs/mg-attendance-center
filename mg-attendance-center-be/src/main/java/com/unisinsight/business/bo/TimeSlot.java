package com.unisinsight.business.bo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 时间段
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/4
 * @since 1.0
 */
@Getter
@Setter
public class TimeSlot {
    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
