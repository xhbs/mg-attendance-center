package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.QuerySubsidStuResultBO;
import com.unisinsight.business.dto.SubdisStuResultCountDTO;
import com.unisinsight.business.model.SubsidStuAttendanceResultsDO;
import com.unisinsight.framework.common.base.Mapper;

import java.util.List;

public interface SubsidStuAttendanceResultsMapper extends Mapper<SubsidStuAttendanceResultsDO> {

    List<SubdisStuResultCountDTO> findSubsidStuResultList(QuerySubsidStuResultBO queryBO);
}