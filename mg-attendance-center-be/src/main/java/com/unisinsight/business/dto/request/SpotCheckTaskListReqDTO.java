package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 抽查考勤任务分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpotCheckTaskListReqDTO extends PaginationReq {

    @ApiModelProperty(name = "state", value = "状态： 1未开始 2进行中 3已结束", example = "2")
    private Integer state;

    @ApiModelProperty(name = "school_year", value = "学年", required = true, example = "2021-2022")
    @NotNull
    private String schoolYear;

    @ApiModelProperty(name = "semester", value = "学期，不限则不传： 春季、秋季", example = "秋季")
    private String semester;

    @ApiModelProperty(name = "month", value = "月份，不限则不传：yyyy-MM", example = "2021-01")
    private String month;

    @ApiModelProperty(name = "creator_org_index_path", value = "创建人组织索引", example = "0/1/530000/530100/")
    private String creatorOrgIndexPath;

    @ApiModelProperty(name = "search", value = "模糊搜索，任务名称、考勤目标组织名称、创建人姓名")
    private String search;

    /**
     * 当前用户管理员级别
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Short adminLevel;

    /**
     * 当前用户相关的组织索引
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String userOrgIndexPath;

    /**
     * 当前用户相关的组织编号（省/市/区）
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private List<String> userOrgIndexes;

    /**
     * 考勤日期开始日期
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private LocalDate startDate;

    /**
     * 考勤日期结束日期
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private LocalDate endDate;
}
