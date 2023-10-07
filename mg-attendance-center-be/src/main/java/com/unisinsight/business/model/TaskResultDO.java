package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 抽查任务结果表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
@Data
@Table(name = "task_results")
public class TaskResultDO {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 考勤结果
     */
    @Column(name = "result")
    private Integer result;

    /**
     * 考勤日期
     */
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private Integer taskId;


    /**
     * 人员编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 人员姓名
     */
    @Column(name = "person_name")
    private String personName;

    /**
     * 人员所属组织
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 打卡记录ID
     */
    @Column(name = "original_record_id")
    private Long originalRecordId;

    /**
     * 打卡时间
     */
    @Column(name = "captured_at")
    private LocalDateTime capturedAt;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}