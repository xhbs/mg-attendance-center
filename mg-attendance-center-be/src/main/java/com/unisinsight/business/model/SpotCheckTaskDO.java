package com.unisinsight.business.model;

import com.unisinsight.business.common.enums.SpotCheckTaskState;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 抽查任务表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@Data
@Table(name = "spot_check_tasks")
public class SpotCheckTaskDO {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 任务名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 状态： 1未开始 2进行中 3已结束 {@link SpotCheckTaskState}
     */
    @Column(name = "state")
    private Integer state;

    /**
     * 开始日期
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * 抽查天数
     */
    @Column(name = "day_count")
    private Integer dayCount;

    /**
     * 学年
     */
    @Column(name = "school_year")
    private String schoolYear;

    /**
     * 学期
     */
    @Column(name = "semester")
    private String semester;

    /**
     * 月份
     */
    @Column(name = "month")
    private String month;

    /**
     * 缺勤率最小值
     */
    @Column(name = "minimum_absence_rate")
    private Integer minimumAbsenceRate;

    /**
     * 创建人编号
     */
    @Column(name = "creator_code")
    private String creatorCode;

    /**
     * 创建人姓名
     */
    @Column(name = "creator_name")
    private String creatorName;

    /**
     * 创建人组织索引
     */
    @Column(name = "creator_org_index_path")
    private String creatorOrgIndexPath;

    /**
     * 创建人组织名称
     */
    @Column(name = "creator_org_name")
    private String creatorOrgName;

    /**
     * 创建人角色名称
     */
    @Column(name = "creator_role_name")
    private String creatorRoleName;

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

    @Column(name = "call_the_roll")
    private Integer callTheRoll;
    @Column(name = "call_the_roll_first_task_id")
    private Integer callTheRollFirstTaskId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotCheckTaskDO that = (SpotCheckTaskDO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
