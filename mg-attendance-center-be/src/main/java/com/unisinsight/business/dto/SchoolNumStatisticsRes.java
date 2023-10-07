package com.unisinsight.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName : mg-attendance-center
 * @Description : 学校数量统计返回
 * @Author : xiehb
 * @Date: 2022/11/07 14:37
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolNumStatisticsRes {
    private Integer provinceSchoolCount;
    private Integer citySchoolCount;
    private Integer count;
    private BigDecimal provinceSchoolPercent;
    private BigDecimal citySchoolCountPercent;
}
