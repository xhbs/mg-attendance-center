package com.unisinsight.business.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 日期范围
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeBO {
    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;
}
