package com.unisinsight.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName : mg-attendance-center
 * @Description : 省级整学期统计查询结果
 * @Author : xiehb
 * @Date: 2022/11/04 11:36
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermStatisticsProvince {
    private Integer schoolTerm;
    private StatisticsProvince data;
}
