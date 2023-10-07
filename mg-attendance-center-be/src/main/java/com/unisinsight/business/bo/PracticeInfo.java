package com.unisinsight.business.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 实习信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
@Data
public class PracticeInfo {
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
}
