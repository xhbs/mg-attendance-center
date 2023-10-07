package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class SpotCheckTaskRecordDTO {
    private Short result;
    private String taskName;
    private Integer taskId;
    private String orgIndex;
    private String personNo;
    private String personName;
    private String month;
    private LocalDate attendanceDate;

}
