package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实习学生
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/16
 */
@Data
public class PracticeStudentDTO {
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
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 上报时间
     */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportedAt;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_time", value = "开始日期")
    @JsonProperty("start_time")
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(name = "end_time", value = "结束日期")
    @JsonProperty("end_time")
    private String endDate;

    /**
     * 人员编号
     */
    @ApiModelProperty(value = "人员编号")
    private String personNo;

    /**
     * 人员姓名
     */
    @ApiModelProperty(value = "人员姓名")
    private String personName;

    /**
     * 人员照片
     */
    @ApiModelProperty(value = "人员照片")
    private String personUrl;

}
