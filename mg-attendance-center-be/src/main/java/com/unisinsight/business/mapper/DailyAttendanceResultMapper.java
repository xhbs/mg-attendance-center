package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.HistoryItem;
import com.unisinsight.business.bo.PersonBO;
import com.unisinsight.business.model.DailyAttendanceResultDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 日常考勤结果表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
public interface DailyAttendanceResultMapper extends Mapper<DailyAttendanceResultDO>,
        InsertUseGeneratedKeysMapper<DailyAttendanceResultDO>, PartitionByTimeTableMapper {

    /**
     * 批量保存
     */
    void batchSave(@Param("results") List<DailyAttendanceResultDO> results);

    /**
     * 查找日常考勤缺勤的人员（只查询在校的人员）
     */
    List<PersonBO> findAbsencePersonsOfDate(@Param("date") LocalDate date);

    /**
     * 查询历史考勤记录
     */
    List<HistoryItem> findHistoryOfPerson(@Param("personNo") String personNo,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

}
