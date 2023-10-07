package com.unisinsight.business.bo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonListOfClassBO extends PersonOfClassBO {
    /**
     * 学生图片
     */
    private String personUrl;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 点名日期
     */
    private LocalDate attendanceDate;

    /**
     * 学生点名结果
     */
    private Integer result;

    private Integer status;

    private Long taskResultId;
}
