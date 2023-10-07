package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 实习点名详情查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PracticeAttendancePersonListReqDTO extends PaginationReq {

    @ApiModelProperty("点名状态, -1暂未考勤 0考勤正常 99 考勤缺勤")
    private Integer attendanceResult;

    @ApiModelProperty("申请时间范围最小值 yyyy-MM-dd")
    private LocalDate reportTimeMin;

    @ApiModelProperty("申请时间范围最大值 yyyy-MM-dd")
    private LocalDate reportTimeMax;

    @ApiModelProperty("模糊搜索，学生姓名/学号")
    private String search;

    /**
     * 用户编号
     */
    @ApiModelProperty(hidden = true)
    private String userCode;

    public LocalDateTime getReportTimeMin() {
        if (reportTimeMin == null) {
            return null;
        }
        return LocalDateTime.of(reportTimeMin, LocalTime.MIN);
    }

    public LocalDateTime getReportTimeMax() {
        if (reportTimeMax == null) {
            return null;
        }
        return LocalDateTime.of(reportTimeMax, LocalTime.MAX);
    }
}
