package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.QuerySubsidMatchStaticStuBO;
import com.unisinsight.business.dto.SubdisLevelResultCountDTO;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticStuResDTO;
import com.unisinsight.business.model.SubsidMatchStaticStuDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SubsidMatchStaticStuMapper extends Mapper<SubsidMatchStaticStuDO> {

    /**
      *@description 汇总统计节点数据
      *@param
      *@return
      *@date    2021/9/2 21:03
      */
    List<SubdisLevelResultCountDTO> selectCountStuRecords(@Param(value="orgIndexList")List<String>orgIndexList
            ,@Param(value="subsidRuleId")Integer subsidRuleId);

    List<SubsidMatchStaticStuResDTO> selectSubsidStaticStuList(QuerySubsidMatchStaticStuBO queryBO);


    void generateSubsidStuAttendanceResults(SubsidBaseReqDTO reqDTO);
}