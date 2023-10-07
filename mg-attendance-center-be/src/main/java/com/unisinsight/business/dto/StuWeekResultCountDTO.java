package com.unisinsight.business.dto;

import lombok.Data;

/**
 * @author tanggang
 * @version 1.0
 *
 * @email tang.gang@inisinsight.com
 * @date 2021/8/16 16:49
 **/
@Data
public class StuWeekResultCountDTO {
    private String personNo;
    private Short normalWeeks;
    private Short absentWeeks;
}
