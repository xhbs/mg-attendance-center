package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 抽查任务关联组织表
 */
@Data
@Table(name = "task_org_relations")
public class TaskOrgRelationDO {
    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private Integer taskId;

    /**
     * 考勤目标组织编码
     */
    @Column(name = "org_index")
    private String orgIndex;
}