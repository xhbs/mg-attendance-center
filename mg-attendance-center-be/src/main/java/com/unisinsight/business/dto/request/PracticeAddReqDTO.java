package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unisinsight.business.dto.response.AttachFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 实习申请 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@Data
public class PracticeAddReqDTO {

    /**
     * 人员列表
     */
    @ApiModelProperty(value = "人员列表", required = true)
    @NotEmpty
    private List<String> personNos;

    /**
     * 实习开始时间
     */
    @ApiModelProperty(name = "start_time", value = "实习开始时间,yyyy-MM-dd", required = true, example = "2020-12-14")
    @NotNull
    @JsonProperty("start_time")
    private LocalDate startDate;

    /**
     * 实习结束时间
     */
    @ApiModelProperty(name = "end_time", value = "实习结束时间,yyyy-MM-dd", required = true, example = "2020-12-14")
    @NotNull
    @JsonProperty("end_time")
    private LocalDate endDate;

    /**
     * 实习单位
     */
    @ApiModelProperty(value = "实习单位", required = true)
    private String practiceCompany;

    /**
     * 单位联系人
     */
    @ApiModelProperty(value = "单位联系人", required = true)
    private String companyContacts;

    /**
     * 单位联系电话
     */
    @ApiModelProperty(value = "单位联系电话", required = true)
    private String contactsPhone;

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
