package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 考勤明细详情 入参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
public class DailyAttendanceDetailReqDTO {

    @ApiModelProperty(name = "person_no", value = "学生人员编号", required = true)
    @NotNull
    private String personNo;

    @ApiModelProperty(name = "attendance_date", value = "考勤日期，yyyy-MM-dd", required = true)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;
}
