package com.unisinsight.business.dto.request;

import lombok.Data;

/**
 * @ClassName : mg-attendance-center
 * @Description : 统计接口入参
 * @Author : xiehb
 * @Date: 2022/11/04 09:09
 * @Version 1.0.0
 */

@Data
public class StatisticsReq {
    //学期(0-春季,1-秋季)
    private Integer schoolTerm;
    //学年
    private String schoolYear;
    //考勤周期
    private Integer checkWeek;

    private Boolean isJob;
}
