package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 抽查考勤明细详情 出参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpotAttendanceDetailResDTO extends DailyAttendanceDetailResDTO {

    @ApiModelProperty(name = "task_info", value = "考勤任务信息", required = true)
    private TaskInfo taskInfo;

    @Getter
    @Setter
    public static class TaskInfo {
        @ApiModelProperty(name = "task_id", value = "考勤任务ID", required = true)
        private Integer taskId;

        @ApiModelProperty(name = "task_name", value = "考勤任务名称", required = true)
        private String taskName;

        @ApiModelProperty(name = "creator_name", value = "创建人姓名", required = true, example = "admin")
        private String creatorName;

        @ApiModelProperty(name = "creator_role_name", value = "创建人角色", required = true, example = "省级管理员")
        private String creatorRoleName;

        @ApiModelProperty(name = "creator_org_name", value = "创建人组织名称", required = true, example = "云南省-昆明市-五华区")
        private String creatorOrgName;
    }
}
