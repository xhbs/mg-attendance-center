package com.unisinsight.business.bo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/19
 */
@Data
public class FindStuWeekResultParamBO {
    private LocalDate attendanceStartDate;
    private LocalDate attendanceEndDate;
    private List<String> personNoList;
}
