package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 日常考勤结果和抽查考勤结果公共字段
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/23
 * @since 1.0
 */
@Data
public class BaseAttendanceResult {

    /**
     * 主键id
     */
    @Id
    private Long id;

    /**
     * 任务id
     */
    @Column(name = "task_id")
    private Long taskId;

    /**
     * 抽查任务
     */
    @Column(name = "task_name")
    private String taskName;

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
     * 组织编码
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 组织名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 组织编码
     */
    @Column(name = "index_path")
    private String indexPath;

    /**
     * 组织名称（所有）
     */
    @Column(name = "index_path_name")
    private String indexPathName;

    /**
     * 考勤结果 {@link com.unisinsight.business.common.enums.AttendanceResult}
     */
    @Column(name = "attendance_result")
    private Integer attendanceResult;

    /**
     * 打卡时间
     */
    @Column(name = "pass_time")
    private LocalDateTime passTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
