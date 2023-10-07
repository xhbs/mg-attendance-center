package com.unisinsight.business.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 流程审批记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/26
 */
@Data
@Table(name = "approve_records")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveRecordDO {
    /**
     * 主键
     */
    @Column(name = "id")
    @Id
    private Integer id;

    /**
     * 所属记录ID
     */
    @Column(name = "target_id")
    private Integer targetId;

    /**
     * 记录类型：1请假 2实习 3申诉
     */
    @Column(name = "target_type")
    private Integer targetType;

    /**
     * 流程顺序
     */
    @Column(name = "serial_no")
    private Integer serialNo;

    /**
     * 流程名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 处理结果：1通过 2拒绝
     */
    @Column(name = "result")
    private Integer result;

    /**
     * 审批备注
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 受理用户编号
     */
    @Column(name = "assignee_user_code")
    private String assigneeUserCode;

    /**
     * 受理用户姓名
     */
    @Column(name = "assignee_user_name")
    private String assigneeUserName;

    /**
     * 受理角色编号
     */
    @Column(name = "assignee_role_code")
    private String assigneeRoleCode;

    /**
     * 受理角色名称
     */
    @Column(name = "assignee_role_name")
    private String assigneeRoleName;

    /**
     * 受理时间
     */
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

}
