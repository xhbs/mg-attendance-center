package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * 申诉记录 分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("申诉记录-分页查询-入参")
public class AppealRecordQueryReqDTO extends ApprovalRecordQueryReqDTO {

    /**
     * 当前管理员等级
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Short adminLevel;

    /**
     * 考勤开始时间
     */
    @ApiModelProperty(value = "考勤开始时间，13位时间戳")
    private Long startTime;

    /**
     * 考勤结束时间
     */
    @ApiModelProperty(value = "考勤结束时间，13位时间戳")
    private Long endTime;

    /**
     * 13位时间戳由 long 转为 LocalDate 处理 sql 查询时区的问题
     */
    @ApiModelProperty(hidden = true)
    public LocalDate getStartDate() {
        return startTime == null ? null : Instant.ofEpochMilli(startTime)
                .atZone(ZoneOffset.ofHours(8))
                .toLocalDate();
    }

    /**
     * 13位时间戳由 long 转为 LocalDateTime 处理 sql 查询时区的问题
     */
    @ApiModelProperty(hidden = true)
    public LocalDate getEndDate() {
        return endTime == null ? null : Instant.ofEpochMilli(endTime)
                .atZone(ZoneOffset.ofHours(8))
                .toLocalDate();
    }
}
