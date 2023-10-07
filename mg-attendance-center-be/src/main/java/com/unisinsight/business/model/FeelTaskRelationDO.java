package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 有感考勤与抽查考勤任务关联表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@Data
@Table(name = "task_person_relations")
public class FeelTaskRelationDO {

    /**
     * 有感考勤ID
     */
    @Column(name = "feel_id")
    private Integer feelId;
    /**
     * 抽查任务ID
     */
    @Column(name = "task_id")
    private Integer taskId;
}
