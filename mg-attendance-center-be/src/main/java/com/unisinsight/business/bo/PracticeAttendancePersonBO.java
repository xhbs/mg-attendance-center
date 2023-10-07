package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 实习点名人员
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/15
 */
@Data
public class PracticeAttendancePersonBO {
    /**
     * 实习记录ID
     */
    private Integer practiceRecordId;

    /**
     * 实习人员ID
     */
    private Integer practicePersonId;

    /**
     * 人员编号
     */
    private String personNo;
}
