package com.unisinsight.business.model;

import com.unisinsight.business.bo.PartitionRange;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日常考勤周考勤结果表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Data
@Table(name = "daily_attendance_week_results")
public class DailyAttendanceWeekResultDO implements PartitionTable{
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 状态：0在校 99缺勤
     */
    @Column(name = "result")
    private Integer result;

    /**
     * 学生编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 学生姓名
     */
    @Column(name = "person_name")
    private String personName;

    /**
     * 组织编码
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 组织编码
     */
    @Column(name = "org_index_path")
    private String orgIndexPath;

    /**
     * 考勤周期-开始日期
     */
    @Column(name = "attendance_start_date")
    private LocalDate attendanceStartDate;

    /**
     * 考勤周期-结束日期
     */
    @Column(name = "attendance_end_date")
    private LocalDate attendanceEndDate;

    /**
     * 周一考勤结果
     */
    @Column(name = "result_of_monday")
    @Nullable
    private Integer resultOfMonday;

    /**
     * 周二考勤结果
     */
    @Column(name = "result_of_tuesday")
    @Nullable
    private Integer resultOfTuesday;

    /**
     * 周三考勤结果
     */
    @Column(name = "result_of_wednesday")
    @Nullable
    private Integer resultOfWednesday;

    /**
     * 周四考勤结果
     */
    @Column(name = "result_of_thursday")
    @Nullable
    private Integer resultOfThursday;

    /**
     * 周五考勤结果
     */
    @Column(name = "result_of_friday")
    @Nullable
    private Integer resultOfFriday;

    /**
     * 记录创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 记录更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 根据考勤时间得到考勤明细应该具体存到哪一张子表里面
     * todo 如果修改分表策略 必须修改该方法
     */
    @Override
    public String getTableSuffix() {
        if (attendanceStartDate == null) {
            return null;
        }
        return PartitionRange.genPartitionRangeByWeek(attendanceStartDate).getTableSuffix();
    }
}
