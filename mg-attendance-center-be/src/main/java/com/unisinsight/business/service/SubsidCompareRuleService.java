/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.response.SubsidBaseResDTO;
import com.unisinsight.business.model.SubsidCompareRuleDO;


/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 14:57:32
 * @since 1.0
 */
public interface SubsidCompareRuleService  {

    SubsidBaseResDTO generateSubsidCompareRule(SubsidBaseReqDTO reqDTO);

    boolean existRecord(SubsidBaseReqDTO reqDTO);

    SubsidCompareRuleDO findById(Integer id);

}
