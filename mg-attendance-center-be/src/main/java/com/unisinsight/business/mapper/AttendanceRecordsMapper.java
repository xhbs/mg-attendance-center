package com.unisinsight.business.mapper;

import com.unisinsight.business.model.FeelAttendanceRecordsDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 有感考勤记录
 *
 * @author jiangnan [jiang.nan@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
public interface AttendanceRecordsMapper extends Mapper<FeelAttendanceRecordsDO> , InsertUseGeneratedKeysMapper<FeelAttendanceRecordsDO> {

    int insertFeel (FeelAttendanceRecordsDO feelAttendanceRecordsDO);
    /**
     * 根据周考勤id 查询
     */
    FeelAttendanceRecordsDO findByResultId(@Param("id") Long id,@Param("personNo")String personNo);

    /**
     * 根据抽查考勤任务id 查询
     */
    FeelAttendanceRecordsDO findByPerson(@Param("id") Integer id,@Param("day")LocalDate day,@Param("personNo")String personNo);
    /**
     * 根据抽查考勤任务id 查询
     */
    List<FeelAttendanceRecordsDO> findByTaskId(@Param("id") Integer id, @Param("day")LocalDate day);
}