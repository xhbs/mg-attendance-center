package com.unisinsight.business.mapper;

import com.unisinsight.business.dto.SubdisLevelResultCountDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.model.SubsidMatchStaticLevelDO;
import com.unisinsight.framework.common.base.Mapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SubsidMatchStaticLevelMapper extends Mapper<SubsidMatchStaticLevelDO> {

    List<SubdisLevelResultCountDTO> selectCountLevelRecords(@Param(value="orgParentIndexList")List<String>orgParentIndexList
            , @Param(value="subsidRuleId")Integer subsidRuleId);

    List<SubsidMatchStaticLevelDO> selectSubsidLevelList(SubsidMatchStaticLevelDO queryDO);

    List<SubsidMatchStaticLevelDO> selectSubsidLevelListByHandle(SubsidMatchStaticLevelReqDTO reqDTO);

    Integer selectTotal(SubsidMatchStaticLevelReqDTO reqDTO);
}