package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.DailyAttendanceStaticLevelBO;

import com.unisinsight.business.dto.request.DailyAttendanceHighLvlStaticQueryReqDTO;
import com.unisinsight.business.model.DailyAttendanceStaticLevelDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DailyAttendanceStaticLevelMapper extends Mapper<DailyAttendanceStaticLevelDO> {

    List<DailyAttendanceStaticLevelBO> selectStaticLevelList(DailyAttendanceStaticLevelBO queryBO)  ;

    Integer selectTotal(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO) ;

    List<DailyAttendanceStaticLevelBO> selectStaticLevelListByHandle(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO)  ;

    Integer checkWeekExist(DailyAttendanceStaticLevelBO queryBO);

    void batchUpdate(@Param("results") List<DailyAttendanceStaticLevelDO> results);

    String getMaxYearMonth(@Param(value="schoolYear")String schoolYear, @Param(value="schoolTerm")String schoolTerm);

}