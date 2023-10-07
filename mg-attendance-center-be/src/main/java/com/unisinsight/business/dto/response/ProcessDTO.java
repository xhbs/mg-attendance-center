package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批流程任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/18
 * @since 1.0
 */
@Data
public class ProcessDTO {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", hidden = true)
    private Integer id;

    /**
     * 任务序号
     */
    @ApiModelProperty(value = "任务序号")
    private int serialNo;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String name;

    /**
     * 是否是当前节点
     */
    @ApiModelProperty(value = "是否是当前节点")
    @Deprecated
    private boolean current;

    /**
     * 是否已完成
     */
    @ApiModelProperty(value = "是否已完成")
    @Deprecated
    private boolean completed;

    /**
     * 到达时间
     */
    @ApiModelProperty(value = "到达时间，13位时间戳")
    private LocalDateTime startedAt;

    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间，13位时间戳")
    private LocalDateTime completedAt;

    /**
     * 审批结果
     */
    @ApiModelProperty(value = "审批结果， 1 通过 2 拒绝")
    private Integer result;

    /**
     * 审批备注
     */
    @ApiModelProperty(value = "审批备注")
    private String comment;

    /**
     * 受理用户编号
     */
    @ApiModelProperty(value = "受理用户编号")
    private String assigneeUserCode;

    /**
     * 受理用户姓名
     */
    @ApiModelProperty(value = "受理用户姓名")
    private String assigneeUserName;

    /**
     * 受理角色编号
     */
    @ApiModelProperty(value = "受理角色编号")
    private String assigneeRoleCode;

    /**
     * 受理角色名称
     */
    @ApiModelProperty(value = "受理角色名称")
    private String assigneeRoleName;
}
