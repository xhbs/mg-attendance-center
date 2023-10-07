package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假、实习、申诉 返回参数公共字段
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/29
 * @since 1.0
 */
@Data
class ApprovalRecordDTO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Integer id;

    /**
     * 审批状态
     */
    @ApiModelProperty(value = "审批状态： 1未上报 2审批中 3同意 4拒绝")
    private Integer status;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_time", value = "开始日期")
    @JsonProperty("start_time")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(name = "end_time", value = "结束日期")
    @JsonProperty("end_time")
    private LocalDate endDate;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 上报时间
     */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportedAt;
}
