package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 申诉记录详情 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11 11:23:53
 * @since 1.0
 */
@Data
public class AppealRecordDetailDTO extends AppealRecordListDTO {

    /**
     * 附件文件列表
     */
    @ApiModelProperty(value = "附件文件列表")
    private List<AttachFileDTO> files;

    /**
     * 申诉人员列表
     */
    @ApiModelProperty(value = "申诉人员列表")
    private List<PersonBaseInfoDTO> persons;

    /**
     * 申诉流程
     */
    @ApiModelProperty(value = "申诉流程")
    private List<ProcessDTO> processes;

    /**
     * 流程实例ID
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer processInstanceId;
}
