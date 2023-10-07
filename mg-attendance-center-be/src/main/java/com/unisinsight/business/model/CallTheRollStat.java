package com.unisinsight.business.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.io.Serializable;

/**
 * 点名统计/月(CallTheRollStat)实体类
 *
 * @author XieHaiBo
 * @since 2023-03-22 17:51:52
 */
@Data
public class CallTheRollStat implements Serializable {
    private static final long serialVersionUID = 210459567967279865L;
    /**
     * 月份
     */
    private String month;
    /**
     * 学校编码
     */
    private String schoolOrgIndex;
    /**
     * 学校名称
     */
    private String schoolOrgName;
    /**
     * 学生数
     */
    private Integer studentNum;
    /**
     * 缺勤数
     */
    private Integer absenceNum;
    /**
     * 在校数
     */
    private Integer normalNum;
    /**
     * 请假数
     */
    private Integer leaveNum;
    /**
     * 实习数
     */
    private Integer practiceNum;
    /**
     * 点名任务创建人
     */
    private String taskCreateBy;
    
    private LocalDate createTime;


}

