package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 抽查考勤统计入参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
@ApiModel(value = "抽查列表查询")
public class SpotCheckAttendListReqDTO extends PageParam {

    @ApiModelProperty(name = "status", value = "抽查考勤状态", required = true, example = "0")
    @NotNull
    private Integer status;

    @ApiModelProperty(name = "search_key", value = "搜索字段", example = "昆明")
    private String searchKey;

    @ApiModelProperty(name = "school_year", value = "学年", example = "2021~2022")
    private String schoolYear;

    @ApiModelProperty(name = "semester", value = "学期", example = "秋季")
    private String semester;

    @ApiModelProperty(name = "month", value = "月份", example = "2021-09")
    private String month;
}
