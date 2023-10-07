package com.unisinsight.business.model;

import com.unisinsight.business.bo.PartitionRange;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日常考勤结果表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Data
@Table(name = "daily_attendance_results")
public class DailyAttendanceResultDO implements PartitionTable {
    /**
     * 主键id
     */
    @Id
    private Long id;

    /**
     * 考勤结果：0在校 2实习 3请假 99缺勤
     */
    @Column(name = "result")
    private Integer result;

    /**
     * 考勤日期
     */
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    /**
     * 抓拍时间
     */
    @Column(name = "captured_at")
    private LocalDateTime capturedAt;

    /**
     * 原始记录ID
     */
    @Column(name = "original_record_id")
    private Long originalRecordId;

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
     * 根据考勤日期得到考勤明细应该具体存到哪一张子表里面
     * todo 如果修改分表策略 必须修改该方法
     */
    @Override
    public String getTableSuffix() {
        if (attendanceDate == null) {
            return null;
        }
        return PartitionRange.genPartitionRangeByWeek(attendanceDate).getTableSuffix();
    }
}
