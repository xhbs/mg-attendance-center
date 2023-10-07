/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service.impl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.PersonCountDTO;
import com.unisinsight.business.dto.SubdisLevelResultCountDTO;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelExcelDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticLevelResDTO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.*;
import com.unisinsight.business.service.SubsidMatchStaticLevelService;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 15:06:32
 * @since 1.0
 */
@Service
@Slf4j
public class SubsidMatchStaticLevelServiceImpl implements SubsidMatchStaticLevelService {
    @Resource
    private SubsidMatchStaticLevelMapper subsidMatchStaticLevelMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private SubsidMatchStaticStuMapper subsidMatchStaticStuMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private SubsidCompareRuleMapper subsidCompareRuleMapper;


    @Override
    public PaginationRes<SubsidMatchStaticLevelResDTO> getPage(SubsidMatchStaticLevelReqDTO reqDTO) {
        List<SubsidMatchStaticLevelResDTO> resultList = new ArrayList<>();
        Page page = PageHelper.startPage(reqDTO.getPageNum(), reqDTO.getPageSize(), reqDTO.getOrderBy());
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleMapper.selectByPrimaryKey(reqDTO.getSubsidRuleId());

        SubsidMatchStaticLevelDO queryDO = new SubsidMatchStaticLevelDO();
        queryDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        queryDO.setOrgIndex(reqDTO.getOrgIndex());
        queryDO.setOrgParentIndex(reqDTO.getOrgParentIndex());
        queryDO.setOrgName(reqDTO.getOrgName());
        List<SubsidMatchStaticLevelDO> selectList = subsidMatchStaticLevelMapper.selectSubsidLevelList(queryDO);
        initCheckDt(selectList, resultList, subsidCompareRuleDO);

        return PaginationRes.of(resultList, page);
    }

    @Override
    public PaginationRes<SubsidMatchStaticLevelResDTO> getPageByHandle(SubsidMatchStaticLevelReqDTO reqDTO) {
        List<SubsidMatchStaticLevelResDTO> resultList = new ArrayList<>();

        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleMapper.selectByPrimaryKey(reqDTO.getSubsidRuleId());
        if (reqDTO.getPageNum() == 1) {
            reqDTO.setOffset(reqDTO.getPageSize() * (reqDTO.getPageNum() - 1));
            reqDTO.setLimit(reqDTO.getPageSize() - 1);
        } else {
            reqDTO.setOffset(reqDTO.getPageSize() * (reqDTO.getPageNum() - 1) - 1);
            reqDTO.setLimit(reqDTO.getPageSize());
        }
        SubsidMatchStaticLevelDO queryDO = new SubsidMatchStaticLevelDO();
        queryDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        queryDO.setOrgIndex(reqDTO.getOrgIndex());
        queryDO.setOrgParentIndex(reqDTO.getOrgParentIndex());
        queryDO.setOrgName(reqDTO.getOrgName());
        int total = subsidMatchStaticLevelMapper.selectTotal(reqDTO);
        List<SubsidMatchStaticLevelDO> selectList = subsidMatchStaticLevelMapper.selectSubsidLevelListByHandle(reqDTO);

        if (reqDTO.getPageNum() == 1 && StringUtils.isEmpty(reqDTO.getOrgName())) {//第一页增加合计
            SubsidMatchStaticLevelDO queryParentDO = new SubsidMatchStaticLevelDO();
            queryParentDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
            queryParentDO.setOrgIndex(reqDTO.getOrgParentIndex());
            List<SubsidMatchStaticLevelDO> parentStaticBOList = subsidMatchStaticLevelMapper.selectSubsidLevelList(queryParentDO);
            if (parentStaticBOList.size() <= 0) {
                return PaginationRes.of(null, new Page());
            }
            parentStaticBOList.get(0).setOrgName("合计");
            parentStaticBOList.get(0).setSubType(null);
            initCheckDt(parentStaticBOList, resultList, subsidCompareRuleDO);//添加合计列
        }
        initCheckDt(selectList, resultList, subsidCompareRuleDO);//添加正常查询结果

        return PaginationRes.of(resultList, reqDTO.getPageNum(), reqDTO.getPageSize(), ++total);
    }

    private void initCheckDt(List<SubsidMatchStaticLevelDO> selectList, List<SubsidMatchStaticLevelResDTO> resultList, SubsidCompareRuleDO subsidCompareRuleDO) {
        selectList.forEach(item -> {
            SubsidMatchStaticLevelResDTO resDTO = new SubsidMatchStaticLevelResDTO();
            resDTO.setChkDateSt(subsidCompareRuleDO.getChkDateSt());
            resDTO.setChkDateEd(subsidCompareRuleDO.getChkDateEd());
            resDTO.setMatchNoPassNum(item.getMatchNoPassNum());
            resDTO.setMatchPassNum(item.getMatchPassNum());
            resDTO.setOrgIndex(item.getOrgIndex());
            resDTO.setOrgName(item.getOrgName());
            resDTO.setOrgParentIndex(item.getOrgParentIndex());
            resDTO.setStudentNum(item.getStudentNum());
            resDTO.setSubNum(item.getSubNum());
            resDTO.setSubType(item.getSubType());
            resDTO.setId(item.getId());
            resultList.add(resDTO);
        });
    }

    @Override
    public void generateSubsidLevelResultsNew(SubsidBaseReqDTO reqDTO) {
        OrganizationDO organizationDO = new OrganizationDO();
        organizationDO.setOrgIndex(reqDTO.getOrgIndex());
        List<OrganizationDO> rootOrgDOList = organizationMapper.select(organizationDO);
        if (CollectionUtils.isEmpty(rootOrgDOList)) {
            throw new BaseException("组织信息不存在，组织代码：" + reqDTO.getOrgIndex());
        }
        OrganizationDO rootOrgDO = rootOrgDOList.get(0);
        short subType = 5;
        List<SubsidMatchStaticLevelDO> insertList = new ArrayList<>();
        List<String> orgIndexs = new ArrayList<>();
        Map<String, OrganizationDO> orgParentMap = new HashMap<>(1000);

        Example example = Example.builder(OrganizationDO.class)
                .where(Sqls.custom().andLike("indexPath", rootOrgDO.getIndexPath() + "%"))
                .build();

        List<OrganizationDO> organizationAll = organizationMapper.selectByCondition(example);
        while (rootOrgDO.getSubType() <= subType && subType >= 1) {//只统计省级以下
            int orgOffset = 0;
            short finalSubType = subType;
            List<OrganizationDO> organizationDOS = organizationAll.stream()
                    .filter(e -> e.getSubType() != null && finalSubType == e.getSubType())
                    .collect(Collectors.toList());
            log.info("查询组织层级：{},组织个数:{}，根组织:{}", subType, organizationDOS.size(), reqDTO.getOrgIndex());
            if (CollectionUtils.isEmpty(organizationDOS)) {
                break;
            }
            if (subType == 5) {//班级层级
                Map<String, OrganizationDO> orgMap2 = new HashMap<>(15000);
                organizationDOS.forEach(item -> {
                    orgMap2.put(item.getOrgIndex(), item);
                    orgIndexs.add(item.getOrgIndex());
                });
                //统计班级学生,在籍，在校
                List<PersonCountDTO> personCountList = personMapper.selectCountGroupByOrgIndex(true, true, null);
                //班级人数
                Map<String, Integer> personCountMap = personCountList.parallelStream().collect(Collectors.toMap(PersonCountDTO::getOrgIndex, PersonCountDTO::getPersonNum));
                List<SubdisLevelResultCountDTO> subsidLevelResultCountDTOS = subsidMatchStaticStuMapper.selectCountStuRecords(null, reqDTO.getSubsidRuleId());
                subsidLevelResultCountDTOS.forEach(item -> {
                    OrganizationDO org2 = orgMap2.get(item.getOrgIndex());
                    SubsidMatchStaticLevelDO subsidMatchStaticLevelDO = new SubsidMatchStaticLevelDO();
                    subsidMatchStaticLevelDO.setMatchPassNum(item.getMatchPassNum());
                    subsidMatchStaticLevelDO.setMatchNoPassNum(item.getMatchNoPassNum());
                    subsidMatchStaticLevelDO.setOrgIndex(item.getOrgIndex());
                    subsidMatchStaticLevelDO.setOrgName(org2.getOrgName());
                    subsidMatchStaticLevelDO.setOrgParentIndex(org2.getOrgParentIndex());
                    subsidMatchStaticLevelDO.setStudentNum(personCountMap.getOrDefault(item.getOrgIndex(), 0));
                    subsidMatchStaticLevelDO.setSubNum(item.getMatchPassNum() + item.getMatchNoPassNum());
                    subsidMatchStaticLevelDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
                    subsidMatchStaticLevelDO.setSubType(org2.getSubType());
                    subsidMatchStaticLevelDO.setCreateTime(new Date());
                    insertList.add(subsidMatchStaticLevelDO);
                });
            } else {
                organizationDOS.forEach(item -> {
                    orgParentMap.put(item.getOrgIndex(), item);
                    orgIndexs.add(item.getOrgIndex());
                });
                List<SubdisLevelResultCountDTO> subsidLevelResultCountDTOS = subsidMatchStaticLevelMapper.selectCountLevelRecords(orgIndexs, reqDTO.getSubsidRuleId());
                subsidLevelResultCountDTOS.forEach(item -> {
                    OrganizationDO org2 = orgParentMap.get(item.getOrgParentIndex());
                    SubsidMatchStaticLevelDO subsidMatchStaticLevelDO = new SubsidMatchStaticLevelDO();
                    subsidMatchStaticLevelDO.setOrgIndex(org2.getOrgIndex());//组织标识
                    subsidMatchStaticLevelDO.setOrgName(org2.getOrgName());
                    subsidMatchStaticLevelDO.setSubType(org2.getSubType());
                    subsidMatchStaticLevelDO.setOrgParentIndex(org2.getOrgParentIndex());

                    subsidMatchStaticLevelDO.setSubsidRuleId(reqDTO.getSubsidRuleId());//规则id
                    subsidMatchStaticLevelDO.setMatchPassNum(item.getMatchPassNum());
                    subsidMatchStaticLevelDO.setMatchNoPassNum(item.getMatchNoPassNum());

                    subsidMatchStaticLevelDO.setStudentNum(item.getStudentNum());
                    subsidMatchStaticLevelDO.setSubNum(item.getMatchPassNum() + item.getMatchNoPassNum());
                    subsidMatchStaticLevelDO.setCreateTime(new Date());
                    insertList.add(subsidMatchStaticLevelDO);
                });
            }

            if (CollectionUtils.isNotEmpty(insertList)) {
                log.info("subsid_match_static_level 插入数据条数:{}，组织层级{}", insertList.size(), subType);
                List<List<SubsidMatchStaticLevelDO>> split = CollUtil.split(insertList, 3000);
                split.stream().parallel().forEach(e -> subsidMatchStaticLevelMapper.insertList(e));
            }
            insertList.clear();
            orgParentMap.clear();
            orgIndexs.clear();
            --subType;
        }

    }


    @Override
    public void generateSubsidLevelResults(SubsidBaseReqDTO reqDTO) {
        OrganizationDO organizationDO = new OrganizationDO();
        organizationDO.setOrgIndex(reqDTO.getOrgIndex());
        List<OrganizationDO> rootOrgDOList = organizationMapper.select(organizationDO);
        if (CollectionUtils.isEmpty(rootOrgDOList)) {
            throw new BaseException("组织信息不存在，组织代码：" + reqDTO.getOrgIndex());
        }
        OrganizationDO rootOrgDO = rootOrgDOList.get(0);
        short subType = 5;
        List<SubsidMatchStaticLevelDO> insertList = new ArrayList<>();
        List<String> orgIndexs = new ArrayList<>();
        Map<String, OrganizationDO> orgParentMap = new HashMap<>(1000);

        while (rootOrgDO.getSubType() <= subType && subType >= 1) {//只统计省级以下
            int orgOffset = 0;
            while (true) {
                List<OrganizationDO> organizationDOS = organizationMapper.selectLowerOrgsLimit(reqDTO.getOrgIndex(), subType, 2000, orgOffset);
                log.info("查询组织层级：{},组织个数:{}，根组织:{}", subType, organizationDOS.size(), reqDTO.getOrgIndex());
                if (CollectionUtils.isEmpty(organizationDOS)) {
                    break;
                }
                if (subType == 5) {//班级层级
                    Map<String, OrganizationDO> orgMap2 = new HashMap<>(15000);
                    organizationDOS.forEach(item -> {
                        orgMap2.put(item.getOrgIndex(), item);
                        orgIndexs.add(item.getOrgIndex());
                    });
                    //统计班级学生,在籍，在校
                    List<PersonCountDTO> personCountList = personMapper.selectCountGroupByOrgIndex(true, true, orgIndexs);
                    //班级人数
                    Map<String, Integer> personCountMap = personCountList.parallelStream().collect(Collectors.toMap(PersonCountDTO::getOrgIndex, PersonCountDTO::getPersonNum));
                    List<SubdisLevelResultCountDTO> subsidLevelResultCountDTOS = subsidMatchStaticStuMapper.selectCountStuRecords(orgIndexs, reqDTO.getSubsidRuleId());
                    subsidLevelResultCountDTOS.forEach(item -> {
                        OrganizationDO org2 = orgMap2.get(item.getOrgIndex());
                        SubsidMatchStaticLevelDO subsidMatchStaticLevelDO = new SubsidMatchStaticLevelDO();
                        subsidMatchStaticLevelDO.setMatchPassNum(item.getMatchPassNum());
                        subsidMatchStaticLevelDO.setMatchNoPassNum(item.getMatchNoPassNum());
                        subsidMatchStaticLevelDO.setOrgIndex(item.getOrgIndex());
                        subsidMatchStaticLevelDO.setOrgName(org2.getOrgName());
                        subsidMatchStaticLevelDO.setOrgParentIndex(org2.getOrgParentIndex());
                        subsidMatchStaticLevelDO.setStudentNum(personCountMap.getOrDefault(item.getOrgIndex(), 0));
                        subsidMatchStaticLevelDO.setSubNum(item.getMatchPassNum() + item.getMatchNoPassNum());
                        subsidMatchStaticLevelDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
                        subsidMatchStaticLevelDO.setSubType(org2.getSubType());
                        subsidMatchStaticLevelDO.setCreateTime(new Date());
                        insertList.add(subsidMatchStaticLevelDO);
                    });
                } else {
                    organizationDOS.forEach(item -> {
                        orgParentMap.put(item.getOrgIndex(), item);
                        orgIndexs.add(item.getOrgIndex());
                    });
                    List<SubdisLevelResultCountDTO> subsidLevelResultCountDTOS = subsidMatchStaticLevelMapper.selectCountLevelRecords(orgIndexs, reqDTO.getSubsidRuleId());
                    subsidLevelResultCountDTOS.forEach(item -> {
                        OrganizationDO org2 = orgParentMap.get(item.getOrgParentIndex());
                        SubsidMatchStaticLevelDO subsidMatchStaticLevelDO = new SubsidMatchStaticLevelDO();
                        subsidMatchStaticLevelDO.setOrgIndex(org2.getOrgIndex());//组织标识
                        subsidMatchStaticLevelDO.setOrgName(org2.getOrgName());
                        subsidMatchStaticLevelDO.setSubType(org2.getSubType());
                        subsidMatchStaticLevelDO.setOrgParentIndex(org2.getOrgParentIndex());

                        subsidMatchStaticLevelDO.setSubsidRuleId(reqDTO.getSubsidRuleId());//规则id
                        subsidMatchStaticLevelDO.setMatchPassNum(item.getMatchPassNum());
                        subsidMatchStaticLevelDO.setMatchNoPassNum(item.getMatchNoPassNum());

                        subsidMatchStaticLevelDO.setStudentNum(item.getStudentNum());
                        subsidMatchStaticLevelDO.setSubNum(item.getMatchPassNum() + item.getMatchNoPassNum());
                        subsidMatchStaticLevelDO.setCreateTime(new Date());
                        insertList.add(subsidMatchStaticLevelDO);
                    });
                }

                if (CollectionUtils.isNotEmpty(insertList)) {
                    log.info("subsid_match_static_level 插入数据条数:{}，组织层级{}", insertList.size(), subType);
                    subsidMatchStaticLevelMapper.insertList(insertList);
                }
                insertList.clear();
                orgParentMap.clear();
                orgIndexs.clear();
                orgOffset += 2000;//组织列表位移增加2000
            }
            --subType;
        }

    }

    @Override
    public void exportExcel(Integer subsidRuleId, String orgParentIndex, HttpServletResponse resp) throws IOException {
        SubsidMatchStaticLevelDO querySonDO = new SubsidMatchStaticLevelDO();
        querySonDO.setSubsidRuleId(subsidRuleId);
        querySonDO.setOrgParentIndex(orgParentIndex);
        List<SubsidMatchStaticLevelDO> sonStaticBOList = subsidMatchStaticLevelMapper.selectSubsidLevelList(querySonDO);
        SubsidMatchStaticLevelDO queryParentDO = new SubsidMatchStaticLevelDO();
        queryParentDO.setSubsidRuleId(subsidRuleId);
        queryParentDO.setOrgIndex(orgParentIndex);
        List<SubsidMatchStaticLevelDO> parentStaticBOList = subsidMatchStaticLevelMapper.selectSubsidLevelList(queryParentDO);
        parentStaticBOList.forEach(item -> {
            item.setOrgName("合计");
        });

        parentStaticBOList.addAll(sonStaticBOList);
        if (parentStaticBOList.size() <= 0) {
            throw new BaseException("导出记录不存在");
        }

        List<SubsidMatchStaticLevelExcelDTO> subsidMatchStaticLevelExcelDTOS = JSONArray.parseArray(JSON.toJSONString(parentStaticBOList), SubsidMatchStaticLevelExcelDTO.class);
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleMapper.selectByPrimaryKey(subsidRuleId);
        subsidMatchStaticLevelExcelDTOS.forEach(item -> {
            item.setChkDateRange(subsidCompareRuleDO.getChkDateSt() + "~" + subsidCompareRuleDO.getChkDateEd());
        });
        ExcelWriter writer = getWriter();
        setResp(resp,"资助比对统计");
        writer.write(subsidMatchStaticLevelExcelDTOS).flush(resp.getOutputStream());
        writer.close();
//        ExcelUtil.exportExcel(resp, SubsidMatchStaticLevelExcelDTO.class, subsidMatchStaticLevelExcelDTOS, "资助比对统计");
    }

    private void setResp(HttpServletResponse response,String excelNm) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(excelNm, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
    }

    private ExcelWriter getWriter() {
        ExcelWriter writer = new ExcelWriter(true);
        writer.setColumnWidth(0, 15);
        writer.setColumnWidth(1, 25);
        writer.setColumnWidth(2, 15);
        writer.setColumnWidth(3, 15);
        writer.setColumnWidth(4, 15);
        writer.setColumnWidth(5, 15);
        CellStyle headCellStyle = writer.getHeadCellStyle();
        headCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = writer.createFont();
        font.setBold(true);
        headCellStyle.setFont(font);
        return writer;
    }

    public static void main(String[] args) throws InterruptedException {
        File unzip = ZipUtil.unzip("C:\\Users\\34222\\Desktop\\jvm学习.zip", CharsetUtil.CHARSET_GBK);
        for (File file : unzip.listFiles()) {
            String encode = Base64Encoder.encode(FileUtil.readBytes(file));
            System.out.println(encode);
        }
    }


}
