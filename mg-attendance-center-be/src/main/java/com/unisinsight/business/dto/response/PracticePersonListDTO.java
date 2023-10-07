package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 实习人员统计 分页查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@Data
public class PracticePersonListDTO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Integer id;

    /**
     * 实习状态
     */
    @ApiModelProperty(value = "实习状态，1：待实习，2：实习中，3：实习结束")
    private Short practiceStatus;

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
     * 人员图像
     */
    @ApiModelProperty(value = "人员图像")
    private String personUrl;

    /**
     * 实习开始时间
     */
    @ApiModelProperty(name = "start_time", value = "实习开始时间")
    @JsonProperty("start_time")
    private String startDate;

    /**
     * 实习结束时间
     */
    @ApiModelProperty(name = "end_time", value = "实习结束时间")
    @JsonProperty("end_time")
    private String endDate;

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
     * 上报组织路径
     */
    @ApiModelProperty(value = "上报组织路径")
    private String orgIndexPath;

    /**
     * 上报组织层级名称
     */
    @ApiModelProperty(value = "上报组织层级名称")
    private String orgPathName;
}
