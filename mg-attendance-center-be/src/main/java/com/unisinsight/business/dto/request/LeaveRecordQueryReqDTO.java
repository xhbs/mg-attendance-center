package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 请假记录分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("请假记录-分页查询-入参")
public class LeaveRecordQueryReqDTO extends ApprovalRecordQueryReqDTO {

    /**
     * 请假类型
     */
    @ApiModelProperty(value = "请假类型; 1：病假，2：事假，3：实习登记，99：其他")
    private Short type;

    /**
     * 分组编号列表
     */
    @ApiModelProperty(value = "分组编号列表")
    private List<String> departmentCodes;

    /**
     * 请假开始时间
     */
    @ApiModelProperty(value = "请假开始时间，13位时间戳")
    private Long startTime;

    /**
     * 请假结束时间
     */
    @ApiModelProperty(value = "请假结束时间，13位时间戳")
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
     * 13位时间戳由 long 转为 LocalDate 处理 sql 查询时区的问题
     */
    @ApiModelProperty(hidden = true)
    public LocalDate getEndDate() {
        return endTime == null ? null : Instant.ofEpochMilli(endTime)
                .atZone(ZoneOffset.ofHours(8))
                .toLocalDate();
    }
}
