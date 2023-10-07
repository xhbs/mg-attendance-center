package com.unisinsight.business.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 每天每个组织的统计数据
 */
@Data
public class OneDayResultTaskBo {

    /**
     * 每天考勤总数
     */
    private int total;

    /**
     * 每天请假次数
     */
    private int leave;

    /**
     * 每天缺勤次数
     */
    private int absence;

    /**
     * 每天正常考勤天数
     */
    private int normal;

    /**
     * 每天缺勤率
     */
    private BigDecimal absenceRate;
}
