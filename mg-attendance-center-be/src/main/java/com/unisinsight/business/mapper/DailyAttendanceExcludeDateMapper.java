package com.unisinsight.business.mapper;

import com.unisinsight.business.model.DailyAttendanceExcludeDateDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 日常考勤排除日期表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
public interface DailyAttendanceExcludeDateMapper extends Mapper<DailyAttendanceExcludeDateDO>,
        InsertUseGeneratedKeysMapper<DailyAttendanceExcludeDateDO> {

    /**
     * 判断一个日期是否是排除日期
     */
    boolean isExcludeDate(@Param("date") LocalDate date, @Param("type") Integer type);

    /**
     * 过滤掉排除日期
     */
    List<LocalDate> filterExcludeDates(@Param("type") Integer type, @Param("dates") List<LocalDate> dates);
}
