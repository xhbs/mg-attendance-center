package com.unisinsight.business.dto.request;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName : mg-attendance-center
 * @Description : 学期统计入参
 * @Author : xiehb
 * @Date: 2022/11/04 09:09
 * @Version 1.0.0
 */

@Data
@ToString(callSuper = true)
public class StatisticsTermReq extends StatisticsReq{
    private String orgIndex;
    private String type;
}
