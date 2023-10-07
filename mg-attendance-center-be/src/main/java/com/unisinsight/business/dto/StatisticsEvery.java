package com.unisinsight.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName : mg-attendance-center
 * @Description : 校级统计查询结果
 * @Author : xiehb
 * @Date: 2022/11/04 11:36
 * @Version 1.0.0
 */
@Data
public class StatisticsEvery extends StatisticsProvince{
    private String name;
    private String orgIndex;
}
