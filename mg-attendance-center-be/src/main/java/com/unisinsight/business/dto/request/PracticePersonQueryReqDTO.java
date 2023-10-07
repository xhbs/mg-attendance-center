package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * 实习统计 分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PracticePersonQueryReqDTO extends ApprovalRecordQueryReqDTO {
    /**
     * 实习状态
     */
    @ApiModelProperty(value = "实习状态; 1：待实习，2：正在实习，3：实习结束")
    private Short practiceStatus;

    /**
     * 班级org_index集合
     */
    @ApiModelProperty(value = "班级org_index集合")
    private List<String> classOrgIndexes;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_time", value = "开始日期, yyyy-MM-dd")
    @JsonProperty("start_time")
    @ApiParam("start_time")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(name = "end_time", value = "结束日期, yyyy-MM-dd")
    @JsonProperty("end_time")
    @ApiParam("end_time")
    private LocalDate endDate;
}
