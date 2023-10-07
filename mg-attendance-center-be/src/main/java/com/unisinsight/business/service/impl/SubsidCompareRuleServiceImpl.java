/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.unisinsight.business.bo.SubsidCompareRuleBO;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.response.SubsidBaseResDTO;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.mapper.SubsidCompareRuleMapper;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.model.SubsidCompareRuleDO;
import com.unisinsight.business.service.SubsidCompareRuleService;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 14:57:32
 * @since 1.0
 */
@Service
public class SubsidCompareRuleServiceImpl implements SubsidCompareRuleService {
    @Resource
    private SubsidCompareRuleMapper subsidCompareRuleMapper;
    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public SubsidBaseResDTO generateSubsidCompareRule(SubsidBaseReqDTO reqDTO) {
        SubsidCompareRuleDO insertDO = new SubsidCompareRuleDO();
        insertDO.setOrgIndex(reqDTO.getOrgIndex());
        insertDO.setAbsentRate(reqDTO.getAbsentRate());
        insertDO.setChkDateEd(reqDTO.getChkDateEd());
        insertDO.setChkDateSt(reqDTO.getChkDateSt());
        insertDO.setSubsidType(reqDTO.getSubsidType());
        insertDO.setSubListIndex(reqDTO.getSubListIndex());
        insertDO.setSchoolYear(reqDTO.getSchoolYear());
        insertDO.setSchoolTerm(reqDTO.getSchoolTerm());
        insertDO.setCallTheRollAbsentRate(reqDTO.getCallTheRollAbsentRate());
        insertDO.setRule(reqDTO.getRule());
        subsidCompareRuleMapper.insertSubsidCompareRule(insertDO);
        reqDTO.setSubsidRuleId(insertDO.getId());//设置生成id
        return new SubsidBaseResDTO(insertDO.getId());
    }

    @Override
    public boolean existRecord(SubsidBaseReqDTO reqDTO) {
        OrganizationDO queryOrgDO = new OrganizationDO();
        queryOrgDO.setOrgIndex(reqDTO.getOrgIndex());
        List<OrganizationDO> organizationDOList = organizationMapper.select(queryOrgDO);
        if (CollUtil.isEmpty(organizationDOList)) {
            return false;
        }
        OrganizationDO organizationDO = organizationDOList.get(0);
        Short subType = organizationDO.getSubType();
        List<String> orgIndexs = new ArrayList<>();
        orgIndexs.add(reqDTO.getOrgIndex());
        while (subType > 1) {//查询上级节点是否生成
            --subType;
            OrganizationDO orgParentDO = organizationMapper.selectSuperior(reqDTO.getOrgIndex(), subType);
            if (ObjectUtils.isEmpty(orgParentDO)) {
                continue;
            }
            orgIndexs.add(orgParentDO.getOrgIndex());
        }
        SubsidCompareRuleBO queryDO = new SubsidCompareRuleBO();
        queryDO.setSubsidType(reqDTO.getSubsidType());
        queryDO.setSubListIndex(reqDTO.getSubListIndex());
        queryDO.setAbsentRate(reqDTO.getAbsentRate());
        queryDO.setChkDateSt(reqDTO.getChkDateSt());
        queryDO.setChkDateEd(reqDTO.getChkDateEd());
        if (reqDTO.getRule() != null) {
            queryDO.setRule(reqDTO.getRule());
        }
        queryDO.setOrgIndexs(orgIndexs);
        List<SubsidCompareRuleDO> selectList = subsidCompareRuleMapper.selectSubsidCompareRuleList(queryDO);
        if (CollectionUtils.isNotEmpty(selectList)) {
            reqDTO.setSubsidRuleId(selectList.get(0).getId());
            return true;
        }
        return false;
    }

    @Override
    public SubsidCompareRuleDO findById(Integer id) {
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleMapper.selectByPrimaryKey(id);
        return subsidCompareRuleDO;
    }
}
