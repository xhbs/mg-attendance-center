package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 抽查考勤任务与人员关联表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@Data
@Table(name = "task_person_relations")
public class TaskPersonRelationDO {

    /**
     * 抽查任务ID
     */
    @Column(name = "task_id")
    private Integer taskId;

    /**
     * 人员编号
     */
    @Column(name = "person_no")
    private String personNo;
}
