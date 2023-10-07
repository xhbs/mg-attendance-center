package com.unisinsight.business.dto.request;

import com.unisinsight.business.dto.response.AttachFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 请假新增 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Data
public class LeaveAddReqDTO {

    /**
     * 人员列表
     */
    @ApiModelProperty(value = "人员列表", required = true)
    @NotEmpty
    private List<String> personNos;

    /**
     * 请假类型
     */
    @ApiModelProperty(value = "请假类型; 1：病假，2：事假，3：实习登记，99：其他", required = true)
    @NotNull
    private Short type;

    /**
     * 请假开始时间
     */
    @ApiModelProperty(value = "请假开始时间，13位时间戳", required = true)
    @NotNull
    private Long startTime;

    /**
     * 请假结束时间
     */
    @ApiModelProperty(value = "请假结束时间，13位时间戳", required = true)
    @NotNull
    private Long endTime;

    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因")
    private String reason;

    /**
     * 附件文件列表
     */
    @ApiModelProperty(value = "附件文件列表")
    private List<AttachFileDTO> files;

    /**
     * 是否开始上报
     */
    @ApiModelProperty(value = "是否开始上报")
    private boolean report;
}
