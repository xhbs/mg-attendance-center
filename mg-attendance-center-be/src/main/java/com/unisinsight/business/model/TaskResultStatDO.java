package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 抽查考勤统计表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/6
 */
@Data
@Table(name = "task_results_stat")
public class TaskResultStatDO {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 任务id
     */
    @Column(name = "task_id")
    private Integer taskId;

    /**
     * 考勤日期
     */
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    /**
     * 学校组织编码
     */
    @Column(name = "school_org_index")
    private String schoolOrgIndex;

    /**
     * 学校名称
     */
    @Column(name = "school_org_name")
    private String schoolOrgName;

    /**
     * 所属组织索引
     */
    @Column(name = "index_path")
    private String indexPath;

    /**
     * 所属组织名称
     */
    @Column(name = "index_path_name")
    private String indexPathName;

    /**
     * 缺勤率
     */
    @Column(name = "absence_rate")
    private Double absenceRate;

    /**
     * 抽查学生总数
     */
    @Column(name = "student_num")
    private Integer studentNum;

    /**
     * 缺勤学生数
     */
    @Column(name = "absence_num")
    private Integer absenceNum;

    /**
     * 在校学生数
     */
    @Column(name = "normal_num")
    private Integer normalNum;

    /**
     * 请假学生数
     */
    @Column(name = "leave_num")
    private Integer leaveNum;

    /**
     * 实习学生数
     */
    @Column(name = "practice_num")
    private Integer practiceNum;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}