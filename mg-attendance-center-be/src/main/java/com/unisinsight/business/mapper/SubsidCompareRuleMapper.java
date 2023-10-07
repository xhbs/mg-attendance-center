package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.SubsidCompareRuleBO;
import com.unisinsight.business.model.SubsidCompareRuleDO;
import com.unisinsight.framework.common.base.Mapper;

import java.util.List;

public interface SubsidCompareRuleMapper extends Mapper<SubsidCompareRuleDO> {

    List<SubsidCompareRuleDO> selectSubsidCompareRuleList(SubsidCompareRuleBO queryBO);

    Integer insertSubsidCompareRule(SubsidCompareRuleDO insertDO);
}