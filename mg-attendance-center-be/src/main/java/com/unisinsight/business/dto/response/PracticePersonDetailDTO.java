package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 实习人员统计详情 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PracticePersonDetailDTO extends PracticePersonListDTO {

    /**
     * 实习申请记录ID
     */
    @ApiModelProperty(value = "实习申请记录ID", hidden = true)
    private Integer practiceRecordId;

    /**
     * 人员分组编号
     */
    @ApiModelProperty(name = "department_code", value = "人员分组编号")
    @JsonProperty("department_code")
    private String orgIndex;

    /**
     * 人员分组名称
     */
    @ApiModelProperty(name = "department_name", value = "人员分组名称")
    @JsonProperty("department_name")
    private String orgName;

    /**
     * 上报人编号
     */
    @ApiModelProperty(value = "上报人编号")
    private String creatorCode;

    /**
     * 上报人编号
     */
    @ApiModelProperty(value = "上报人姓名")
    private String creatorName;

    /**
     * 上报人角色
     */
    @ApiModelProperty(value = "上报人角色")
    private String creatorRoleName;

    /**
     * 上报时间
     */
    @ApiModelProperty(value = "上报时间")
    private String reportedAt;

    /**
     * 处理流程
     */
    @ApiModelProperty(value = "处理流程")
    private List<ProcessDTO> processes;

    /**
     * 附件文件列表
     */
    @ApiModelProperty(value = "附件文件列表")
    private List<AttachFileDTO> files;
}
