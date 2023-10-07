package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.DailyAttendancePeriodStuBO;
import com.unisinsight.business.model.DailyAttendancePeriodStuDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyAttendancePeriodStuMapper extends Mapper<DailyAttendancePeriodStuDO>,PartitionByTimeTableMapper {

    List<DailyAttendancePeriodStuDO> findStuAttendanceList(DailyAttendancePeriodStuBO queryBO);

    int countByConditions(DailyAttendancePeriodStuBO queryBO);

    void batchUpdate(@Param("results") List<DailyAttendancePeriodStuDO> results);

}