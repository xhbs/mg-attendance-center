package com.unisinsight.business.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName : mg-attendance-center
 * @Description : 统计接口入参
 * @Author : xiehb
 * @Date: 2022/11/04 09:09
 * @Version 1.0.0
 */

@Data
@ToString(callSuper = true)
public class StatisticsCityReq extends StatisticsReq{
    private String orgIndex;
}
