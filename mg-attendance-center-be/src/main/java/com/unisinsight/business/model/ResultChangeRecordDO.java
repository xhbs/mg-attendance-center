package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 考勤记录变更表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/17
 */
@Data
@Table(name = "result_change_records")
public class ResultChangeRecordDO {
    /**
     * 主键id
     */
    @Id
    private Long id;

    /**
     * 考勤记录ID
     */
    @Column(name = "attendance_result_id")
    private Long attendanceResultId;

    /**
     * 变更前结果
     */
    @Column(name = "result_before_change")
    private Integer resultBeforeChange;

    /**
     * 变更后结果
     */
    @Column(name = "result_after_change")
    private Integer resultAfterChange;

    /**
     * 变更方式：1系统变更 2手动变更
     */
    @Column(name = "mode")
    private Integer mode;

    /**
     * 备注
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 变更时间
     */
    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    /**
     * 变更人
     */
    @Column(name = "changed_by")
    private String changedBy;
}
