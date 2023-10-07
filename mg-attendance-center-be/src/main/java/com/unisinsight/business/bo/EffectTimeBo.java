package com.unisinsight.business.bo;

import lombok.Data;

import java.util.Date;

/**
 * @desc 
 * @author  cn [cheng.nian@unisinsight.com]
 * @time    2020/9/18 16:25
 */
@Data
public class EffectTimeBo {

    /**
     * 匹配开始时间
     */
    private Date startTime;

    private Date startTimeLimit;

    private Long startTimeLong;

    private Long endTimeLong;

    /**
     * 匹配结束时间
     */
    private Date endTime;

    private Date endTimeLimit;

    /**
     * 打卡类型 0未签到 1签到 2迁离
     */
    private Short type;

    /**
     * false没有 true有
     */
    private Boolean has;

    /**
     * 表达式id
     */
    private Long expressId;

    private Integer limit;

    private Integer offset;
}
