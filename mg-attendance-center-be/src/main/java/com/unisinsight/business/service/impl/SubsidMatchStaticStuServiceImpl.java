/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.*;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.SubdisStuResultCountDTO;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticStuExcelDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticStuExcelReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticStuReqDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticStuResDTO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.SubsidCompareRuleDO;
import com.unisinsight.business.model.SubsidMatchStaticStuDO;
import com.unisinsight.business.model.SubsidStuListDO;
import com.unisinsight.business.service.SubsidMatchStaticStuService;
import com.unisinsight.business.service.SubsidStuListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.unisinsight.business.common.constants.Constants.MAXIMUM_EXPORT_SIZE;


/**
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 15:05:21
 * @since 1.0
 */
@Service
@Slf4j
public class SubsidMatchStaticStuServiceImpl implements SubsidMatchStaticStuService {
    @Resource
    private SubsidMatchStaticStuMapper subsidMatchStaticStuMapper;
    @Resource
    private SubsidStuAttendanceResultsMapper subsidStuAttendanceResultsMapper;
    @Resource
    private SubsidStuListService subsidStuListService;
    @Resource
    private SubsidCompareRuleMapper subsidCompareRuleMapper;

    @Override
    public void generateSubsidStuAttendanceResultsNew(SubsidBaseReqDTO reqDTO) {
        subsidMatchStaticStuMapper.generateSubsidStuAttendanceResults(reqDTO);
    }

    @Override
    public void generateSubsidStuAttendanceResults(SubsidBaseReqDTO reqDTO) {
        int pageIndex = 1;
        PaginationReq paginationReq = new PaginationReq();
        paginationReq.setPageNum(pageIndex);
        paginationReq.setPageSize(1000);
        QuerySubsidStuResultBO querySubsidStuResultBO = new QuerySubsidStuResultBO();
        querySubsidStuResultBO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        //查询资助名单学生数据
        PaginationRes<SubsidStuBO> page = subsidStuListService.getPage(reqDTO.getSubListIndex(), paginationReq);
        int totalPageNum = (int) (page.getPaging().getTotal() / paginationReq.getPageSize());
        if (page.getPaging().getTotal() % paginationReq.getPageSize() != 0) {
            ++totalPageNum;
        }
        List<String> personList = new ArrayList<>(1024);
        List<SubsidMatchStaticStuDO> insertList = new ArrayList<>(1536);
        Map<String, SubsidStuBO> stuBOMap = new HashMap<>(1536);
        while (pageIndex <= totalPageNum) {
            List<SubsidStuBO> stuBOList = page.getData();
            querySubsidStuResultBO.setPersonNoList(personList);
            stuBOList.forEach(item -> {
                personList.add(item.getPersonNo());
                stuBOMap.put(item.getPersonNo(), item);
            });
            List<SubdisStuResultCountDTO> subsidStuResultList = subsidStuAttendanceResultsMapper.findSubsidStuResultList(querySubsidStuResultBO);
            for (SubdisStuResultCountDTO item : subsidStuResultList) {
                SubsidStuBO subsidStuBO = stuBOMap.get(item.getPersonNo());
                if (StrUtil.isBlank(subsidStuBO.getOrgIndex())) continue;
                SubsidMatchStaticStuDO insertDO = new SubsidMatchStaticStuDO();
                insertDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
                insertDO.setPersonNo(item.getPersonNo());
                insertDO.setOrgIndex(subsidStuBO.getOrgIndex());
                insertDO.setOrgParentIndex(subsidStuBO.getOrgParentIndex());
                insertDO.setNormalNum(item.getNormalNum());
                insertDO.setAbsentNum(item.getAbsentNum());
                BigDecimal totalNum = new BigDecimal(insertDO.getAbsentNum() + insertDO.getNormalNum());
                BigDecimal absentRate = new BigDecimal(insertDO.getAbsentNum()).divide(totalNum, 2, RoundingMode.DOWN).multiply(new BigDecimal(100));
                insertDO.setAbsentRate(absentRate.intValue());
                if (reqDTO.getRule() != null && reqDTO.getRule().compareTo(0) == 0) {
                    insertDO.setStatus(insertDO.getAbsentRate() >= reqDTO.getAbsentRate() ? "0" : "1");
                } else {
                    insertDO.setStatus(insertDO.getAbsentRate() >= reqDTO.getCallTheRollAbsentRate() ? "0" : "1");
                }
                insertDO.setCreateTime(new Date());
                insertList.add(insertDO);
            }
            if (insertList.size() <= 0) {
                break;
            }
            log.info("subsid_match_static_stu 插入数据条数:{}", insertList.size());
            subsidMatchStaticStuMapper.insertList(insertList);
            insertList.clear();
            personList.clear();
            paginationReq.setPageNum(++pageIndex);
            //再次查询
            page = subsidStuListService.getPage(reqDTO.getSubListIndex(), paginationReq);
        }
    }

    @Override
    public PaginationRes<SubsidMatchStaticStuResDTO> getPage(SubsidMatchStaticStuReqDTO reqDTO) {
        List<SubsidMatchStaticStuResDTO> resultList = new ArrayList<>();
        QuerySubsidMatchStaticStuBO queryBO = new QuerySubsidMatchStaticStuBO();
        queryBO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        queryBO.setOrgParentIndex(reqDTO.getOrgParentIndex());
        queryBO.setOrgIndex(reqDTO.getOrgIndex());
        queryBO.setSearchKey(reqDTO.getSearchKey());
        queryBO.setStatus(reqDTO.getStatus());
        Page page;
        if (StringUtils.isEmpty(reqDTO.getOrderBy())) {
            page = PageHelper.startPage(reqDTO.getPageNum(), reqDTO.getPageSize(), "stu.id");
        } else {
            page = PageHelper.startPage(reqDTO.getPageNum(), reqDTO.getPageSize(), reqDTO.getOrderBy() + ", stu.id");
        }
        List<SubsidMatchStaticStuResDTO> subsidMatchStaticStuResDTOS = subsidMatchStaticStuMapper.selectSubsidStaticStuList(queryBO);
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleMapper.selectByPrimaryKey(reqDTO.getSubsidRuleId());
        subsidMatchStaticStuResDTOS.forEach(item -> {
            item.setChkDateSt(subsidCompareRuleDO.getChkDateSt());
            item.setChkDateEd(subsidCompareRuleDO.getChkDateEd());
            resultList.add(item);
        });

        return PaginationRes.of(resultList, page);
    }

    @Override
    public SubsidMatchStaticStuResDTO findById(Long id) {
        QuerySubsidMatchStaticStuBO queryBO = new QuerySubsidMatchStaticStuBO();
        queryBO.setId(id);
        List<SubsidMatchStaticStuResDTO> subsidMatchStaticStuResDTOS = subsidMatchStaticStuMapper.selectSubsidStaticStuList(queryBO);
        if (CollectionUtils.isEmpty(subsidMatchStaticStuResDTOS)) {
            return null;
        }
        return subsidMatchStaticStuResDTOS.get(0);
    }


    @Override
    public void exportExcel(SubsidMatchStaticStuExcelReqDTO reqDTO, HttpServletResponse resp) {
        QuerySubsidMatchStaticStuBO queryBO = new QuerySubsidMatchStaticStuBO();
        queryBO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        queryBO.setOrgParentIndex(reqDTO.getOrgParentIndex());
        queryBO.setOrgIndex(reqDTO.getOrgIndex());
        queryBO.setSearchKey(reqDTO.getSearchKey());
        queryBO.setStatus(reqDTO.getStatus());
        Page page;
        if (StringUtils.isEmpty(reqDTO.getOrderBy())) {
            page = PageHelper.startPage(1, MAXIMUM_EXPORT_SIZE, "stu.id");
        } else {
            page = PageHelper.startPage(1, MAXIMUM_EXPORT_SIZE, reqDTO.getOrderBy() + ", stu.id");
        }
        List<SubsidMatchStaticStuResDTO> subsidMatchStaticStuResDTOS = subsidMatchStaticStuMapper.selectSubsidStaticStuList(queryBO);
        List<SubsidMatchStaticStuExcelDTO> subsidMatchStaticLevelExcelDTOS = JSONArray.parseArray(JSON.toJSONString(subsidMatchStaticStuResDTOS), SubsidMatchStaticStuExcelDTO.class);
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleMapper.selectByPrimaryKey(reqDTO.getSubsidRuleId());
        subsidMatchStaticLevelExcelDTOS.forEach(item -> {
            if ("0".equals(item.getStatus())) {
                item.setStatus("不通过");
            } else {
                item.setStatus("通过");
            }
            item.setChkDateRange(subsidCompareRuleDO.getChkDateSt() + "~" + subsidCompareRuleDO.getChkDateEd());
            item.setTotalNum(item.getAbsentNum() + item.getNormalNum());
        });
        ExcelUtil.exportExcel(resp, SubsidMatchStaticStuExcelDTO.class, subsidMatchStaticLevelExcelDTOS, "资助比对学生明细");
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal(50).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN).multiply(new BigDecimal(100)).intValue());
    }
}
