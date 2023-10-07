package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 实习记录详情 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PracticeRecordDetailDTO extends PracticeRecordListDTO {

    /**
     * 实习单位
     */
    @ApiModelProperty(value = "实习单位")
    private String practiceCompany;

    /**
     * 单位联系人
     */
    @ApiModelProperty(value = "单位联系人")
    private String companyContacts;

    /**
     * 单位联系电话
     */
    @ApiModelProperty(value = "单位联系电话")
    private String contactsPhone;

    /**
     * 实习人员列表
     */
    @ApiModelProperty(value = "实习人员列表")
    private List<PersonBaseInfoDTO> persons;

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
