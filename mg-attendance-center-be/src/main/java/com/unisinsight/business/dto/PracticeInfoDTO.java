package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 实习信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/20
 */
@Data
public class PracticeInfoDTO {
    /**
     * 实习状态
     */
    @ApiModelProperty(value = "实习状态，1：待实习，2：实习中，3：实习结束")
    private Short practiceStatus;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "start_date", value = "开始日期，yyyy-MM-dd", required = true, example = "2020-08-12")
    private String startDate;

    /**
     * 开始日期
     */
    @ApiModelProperty(name = "end_date", value = "结束日期，yyyy-MM-dd", required = true, example = "2020-08-12")
    private String endDate;

    /**
     * 实习单位
     */
    @ApiModelProperty(name = "practice_company", value = "实习单位", required = true)
    private String practiceCompany;

    /**
     * 单位联系人
     */
    @ApiModelProperty(name = "company_contacts", value = "单位联系人", required = true)
    private String companyContacts;

    /**
     * 单位联系电话
     */
    @ApiModelProperty(name = "contacts_phone", value = "单位联系电话", required = true)
    private String contactsPhone;
}
