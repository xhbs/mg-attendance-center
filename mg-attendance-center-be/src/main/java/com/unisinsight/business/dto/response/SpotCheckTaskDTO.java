package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 抽查考勤任务查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@Data
public class SpotCheckTaskDTO {
    @ApiModelProperty(name = "id", value = "任务ID", required = true, example = "1")
    private Integer id;

    @ApiModelProperty(name = "name", value = "任务名称", required = true, example = "8月抽查考勤")
    private String name;

    @ApiModelProperty(name = "state", value = "状态： 1未开始 2进行中 3已结束", required = true, example = "2")
    private Integer state;

    @ApiModelProperty(name = "start_date", value = "开始日期，yyyy-MM-dd", required = true, example = "2021-08-25")
    private String startDate;

    @ApiModelProperty(name = "end_date", value = "结束日期，yyyy-MM-dd", required = true, example = "2021-09-05")
    private String endDate;

    @ApiModelProperty(name = "day_count", value = "抽查天数", required = true, example = "4")
    private Integer dayCount;

    @ApiModelProperty(name = "target_org_indexes", value = "考勤目标的组织编码集合", required = true)
    private List<String> targetOrgIndexes;

    @ApiModelProperty(name = "target_org_names", value = "考勤目标的组织名称，多个以 、分割", required = true, example = "云南省旅游学校")
    private String targetOrgNames;

    @ApiModelProperty(name = "school_year", value = "学年", required = true, example = "2021-2022")
    private String schoolYear;

    @ApiModelProperty(name = "semester", value = "学期，不限则不传： 春季、秋季", example = "秋季")
    private String semester;

    @ApiModelProperty(name = "month", value = "月份，不限则不传：yyyy-MM", example = "2021-01")
    private String month;

    @ApiModelProperty(name = "minimum_absence_rate", value = "缺勤率最小值", example = "50")
    private Integer minimumAbsenceRate;

    @ApiModelProperty(name = "creator_code", value = "创建人编号", required = true, example = "admin")
    private String creatorCode;

    @ApiModelProperty(name = "creator_name", value = "创建人姓名", required = true, example = "admin")
    private String creatorName;

    @ApiModelProperty(name = "creator_role_name", value = "创建人角色名称", required = true, example = "校级管理员")
    private String creatorRoleName;

    @ApiModelProperty(name = "creator_org_name", value = "创建人组织名称", required = true, example = "云南省-昆明市-五华区")
    private String creatorOrgName;

    @ApiModelProperty(name = "created_at", value = "创建时间，yyyy-MM-dd HH:mm:ss", required = true, example = "2021-04-30 16:32:12")
    private String createdAt;

    @ApiModelProperty(name = "updated_at", value = "最后更新时间，yyyy-MM-dd HH:mm:ss", required = true, example = "2021-04-30 16:32:12")
    private String updatedAt;

    @ApiModelProperty(name = "callTheRoll", value = "是否点名", required = false)
    private Integer callTheRoll;
}
