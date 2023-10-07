package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 请假记录详情 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LeaveRecordDetailDTO extends LeaveRecordListDTO {

    @ApiModelProperty(value = "请假状态, 0请假待生效 1 请假中 2请假结束")
    private Integer leaveState;

    /**
     * 处理流程
     */
    @ApiModelProperty(value = "处理流程")
    private List<ProcessDTO> processes;

    /**
     * 流程实例ID
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer processInstanceId;

    /**
     * 附件文件列表
     */
    @ApiModelProperty(value = "附件文件列表")
    private List<AttachFileDTO> files;
}
