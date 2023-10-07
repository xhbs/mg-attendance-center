package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.QuerySubsidStuBO;
import com.unisinsight.business.bo.SubsidStuBO;
import com.unisinsight.business.bo.SubsidStuCountBO;
import com.unisinsight.business.model.SubsidStuListDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SubsidStuListMapper extends Mapper<SubsidStuListDO> {

    void batchUpdate(@Param("results") List<SubsidStuListDO> results);

    /**
      *@description 查找不合法的学生名单
      *@param
      *@return
      *@date    2021/9/27 19:50
      */
    List<SubsidStuListDO> findNoLegalSubsidStuList(@Param(value = "subListIndex") String  subListIndex);

    List<SubsidStuListDO> findSubsidStuList(QuerySubsidStuBO queryBO);

    List<SubsidStuListDO> findSubsidStuExcelList(QuerySubsidStuBO queryBO);

    List<String> findPersonListBySubListIndex(@Param(value = "subListIndex")String subListIndex);

    List<SubsidStuBO> findSubsidStuListBySubListIndex(@Param(value = "subListIndex")String subListIndex);

    List<String> findOrgIndexListBySubListIndex(@Param(value = "subListIndex") String subListIndex);

    List<SubsidStuCountBO> findSubListIndex();

}