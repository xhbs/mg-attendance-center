/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.*;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.SubsidStuAttendanceResultsExcelDTO;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidStuAttendanceResultsReqDTO;
import com.unisinsight.business.dto.response.SpotCheckTaskRecordDTO;
import com.unisinsight.business.dto.response.SubsidStuAttendanceResultsResDTO;
import com.unisinsight.business.job.CallTheRollStatJob;
import com.unisinsight.business.mapper.DailyAttendanceWeekResultMapper;
import com.unisinsight.business.mapper.SubsidCompareRuleMapper;
import com.unisinsight.business.mapper.SubsidStuAttendanceResultsMapper;
import com.unisinsight.business.mapper.TaskResultMapper;
import com.unisinsight.business.model.SubsidCompareRuleDO;
import com.unisinsight.business.model.SubsidStuAttendanceResultsDO;
import com.unisinsight.business.service.SubsidStuAttendanceResultsService;
import com.unisinsight.business.service.SubsidStuListService;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:04:00
 * @since 1.0
 */
@Slf4j
@Service
public class SubsidStuAttendanceResultsServiceImpl implements SubsidStuAttendanceResultsService {

    @Resource
    private SubsidStuAttendanceResultsMapper subsidStuAttendanceResultsMapper;
    @Resource
    private SubsidCompareRuleMapper subsidCompareRuleMapper;
    @Resource
    private SubsidStuListService subsidStuListService;
    @Resource
    private DailyAttendanceWeekResultMapper dailyAttendanceWeekResultMapper;
    @Resource
    private TaskResultMapper taskResultMapper;
    @Resource
    private Executor zizhuExecutor;


    @Override
    public void generateSubsidStuAttendanceResults(SubsidBaseReqDTO reqDTO) {
        SubsidStuBO queryStuBO = new SubsidStuBO();
        queryStuBO.setSubListIndex(reqDTO.getSubListIndex());
        PaginationReq paginationReq1 = new PaginationReq();
        AtomicInteger pageIndex = new AtomicInteger(1);
        paginationReq1.setPageNum(pageIndex.get());
        paginationReq1.setPageSize(100);
        FindStuWeekResultParamBO queryStuWeekResultBO = new FindStuWeekResultParamBO();
        queryStuWeekResultBO.setAttendanceStartDate(reqDTO.getChkDateSt());
        queryStuWeekResultBO.setAttendanceEndDate(reqDTO.getChkDateEd());
        PaginationRes<String> personListPage1 = subsidStuListService.getPersonNoListPage(reqDTO.getSubListIndex(), paginationReq1);
        int totalPageNum = (int) (personListPage1.getPaging().getTotal() / paginationReq1.getPageSize());
        if (personListPage1.getPaging().getTotal() % paginationReq1.getPageSize() != 0) {
            ++totalPageNum;
        }

        //自动资助比对标识
        boolean autoFlag = reqDTO.getSubsidType() == 0 ? true : false;
        //是否有数据
        AtomicBoolean attendanceExistFlag = new AtomicBoolean(false);
        List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();

        int index = 0;

        while (++index <= totalPageNum) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<SubsidStuAttendanceResultsDO> insertList = new ArrayList<>();
                log.info("pageIndex.get():{}", pageIndex.get());
                PaginationReq paginationReq = new PaginationReq();
                paginationReq.setPageNum(pageIndex.getAndIncrement());
                paginationReq.setPageSize(100);
                PaginationRes<String> personListPage = subsidStuListService.getPersonNoListPage(reqDTO.getSubListIndex(), paginationReq);
                queryStuWeekResultBO.setPersonNoList(personListPage.getData());
                if (ObjectUtil.isNotEmpty(reqDTO.getRule()) && reqDTO.getRule().compareTo(0) == 0) {
                    //日常考勤数据
                    List<SubsidAttendanceResultBO> stuWeekAttendanceResultList = dailyAttendanceWeekResultMapper.findStuWeekAttendanceResultList(queryStuWeekResultBO);
                    stuWeekAttendanceResultList.forEach(item -> {
                        SubsidStuAttendanceResultsDO insertDO = new SubsidStuAttendanceResultsDO();
                        insertDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
                        insertDO.setAttendanceType((short) 1);
                        insertDO.setTaskName("日常考勤");
                        BeanUtils.copyProperties(item, insertDO);
                        insertList.add(insertDO);
                    });
                    //抽查考勤数据
                    List<SpotCheckTaskRecordDTO> spotCheckTaskRecords = taskResultMapper.findDetailList(personListPage.getData(), reqDTO.getChkDateSt(), reqDTO.getChkDateEd(), reqDTO.getRule());
                    spotCheckTaskRecords.forEach(item -> {
                        SubsidStuAttendanceResultsDO insertDO = new SubsidStuAttendanceResultsDO();
                        dayResult(item, insertDO);
                        generate(reqDTO, item, insertDO, 0);
                        insertList.add(insertDO);
                    });
                } else {
                    //点名数据
                    List<SpotCheckTaskRecordDTO> spotCheckTaskRecords = taskResultMapper.findDetailList(personListPage.getData(), reqDTO.getChkDateSt(), reqDTO.getChkDateEd(), reqDTO.getRule());
                    //点名考勤一个月算一次
                    Map<String, List<SpotCheckTaskRecordDTO>> monthMap = spotCheckTaskRecords.stream().collect(Collectors.groupingBy(SpotCheckTaskRecordDTO::getMonth));
                    monthMap.forEach((k, v) -> {
                        //每个月两条考勤
                        //可能存在,同一个学生在多个点名任务中(因为任务包含的组织重复了)
                        Map<String, List<SpotCheckTaskRecordDTO>> personMap = v.stream().collect(Collectors.groupingBy(c -> c.getPersonNo() + "-" + c.getTaskName().split("_")[0]));
                        for (Map.Entry<String, List<SpotCheckTaskRecordDTO>> entry : personMap.entrySet()) {
                            List<SpotCheckTaskRecordDTO> c = entry.getValue();
                            int result;
                            if (c.size() >= 2) {
                                result = CallTheRollStatJob.result(c.get(0).getResult().intValue(), c.get(1).getResult().intValue());
                            } else {
                                result = CallTheRollStatJob.result(c.get(0).getResult().intValue(), null);
                            }
                            SpotCheckTaskRecordDTO item = c.get(0);
                            SubsidStuAttendanceResultsDO insertDO = new SubsidStuAttendanceResultsDO();
                            dayResult(item, insertDO);
                            generate(reqDTO, item, insertDO, 2);
                            insertDO.setResult((short) result);
                            insertList.add(insertDO);
                        }
                    });
                }

                if (!CollectionUtils.isEmpty(insertList)) {
                    attendanceExistFlag.set(true);
                    log.info("subsid_stu_attendance_results 插入数据条数:{}", insertList.size());
                    if (insertList.size() > 1000) {
                        List<SubsidStuAttendanceResultsDO> cutInsertList = new ArrayList(1500);
                        for (int i = 1; i < insertList.size(); i++) {
                            cutInsertList.add(insertList.get(i));
                            if (i % 1000 == 0) {
                                subsidStuAttendanceResultsMapper.insertList(cutInsertList);
                                cutInsertList.clear();
                            }
                        }
                        if (cutInsertList.size() != 0) {
                            subsidStuAttendanceResultsMapper.insertList(cutInsertList);
                        }
                    } else {
                        subsidStuAttendanceResultsMapper.insertList(insertList);
                    }

                }
                insertList.clear();
            }, zizhuExecutor);
            completableFutureList.add(future);
        }
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();
        if (!attendanceExistFlag.get()) {
            if (autoFlag) {
                throw new BaseException("所选名单无日常考勤和抽查考勤数据");
            } else {
                throw new BaseException("上传名单无日常考勤和抽查考勤数据");
            }
        }

    }

    private void dayResult(SpotCheckTaskRecordDTO item, SubsidStuAttendanceResultsDO insertDO) {
        int dayOfWeek = item.getAttendanceDate().getDayOfWeek().getValue();
        switch (dayOfWeek) {
            case 1:
                insertDO.setResultOfMonday(item.getResult());
                break;
            case 2:
                insertDO.setResultOfTuesday(item.getResult());
                break;
            case 3:
                insertDO.setResultOfWednesday(item.getResult());
                break;
            case 4:
                insertDO.setResultOfThursday(item.getResult());
                break;
            case 5:
                insertDO.setResultOfFriday(item.getResult());
                break;
        }
    }

    private void generate(SubsidBaseReqDTO reqDTO, SpotCheckTaskRecordDTO item, SubsidStuAttendanceResultsDO insertDO, int type) {
        insertDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        insertDO.setAttendanceType((short) type);
        insertDO.setTaskRelId(item.getTaskId());
        insertDO.setTaskName(item.getTaskName().split("_")[0]);
        insertDO.setPersonNo(item.getPersonNo());
        insertDO.setPersonName(item.getPersonName());
        insertDO.setResult(item.getResult());
        insertDO.setOrgIndex(item.getOrgIndex());
        insertDO.setAttendanceStartDate(item.getAttendanceDate());
        insertDO.setAttendanceEndDate(item.getAttendanceDate());
        insertDO.setMonth(item.getMonth());
        insertDO.setCreateTime(new Date());
    }

    @Override
    public PaginationRes<SubsidStuAttendanceResultsResDTO> getPage(SubsidStuAttendanceResultsReqDTO reqDTO) {
        Page page = PageHelper.startPage(reqDTO.getPageNum(), reqDTO.getPageSize());

        Integer subsidRuleId = reqDTO.getSubsidRuleId();
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleMapper.selectByPrimaryKey(subsidRuleId);
        if (subsidCompareRuleDO.getSubsidType() == 0) {
            FindStuWeekResultParamBO findStuWeekResultParamBO = new FindStuWeekResultParamBO();
            findStuWeekResultParamBO.setPersonNoList(Collections.singletonList(reqDTO.getPersonNo()));
            findStuWeekResultParamBO.setAttendanceStartDate(subsidCompareRuleDO.getChkDateSt());
            findStuWeekResultParamBO.setAttendanceEndDate(subsidCompareRuleDO.getChkDateEd());
            List<SubsidAttendanceResultBO> list = dailyAttendanceWeekResultMapper.findStuWeekAttendanceResultList(findStuWeekResultParamBO);
            List<SubsidStuAttendanceResultsResDTO> resDTOList = list.stream().map(e -> {
                SubsidStuAttendanceResultsResDTO resDTO = new SubsidStuAttendanceResultsResDTO();
                resDTO.setId(RandomUtil.randomLong());
                resDTO.setResult(e.getResult());
                resDTO.setPersonNo(e.getPersonNo());
                resDTO.setPersonName(e.getPersonName());
                resDTO.setOrgIndex(e.getOrgIndex());
                resDTO.setAttendanceType((short) 1);
                resDTO.setTaskName("日常考勤");
                resDTO.setAttendanceStartDate(e.getAttendanceStartDate());
                resDTO.setAttendanceEndDate(e.getAttendanceEndDate());
                resDTO.setResultOfMonday(e.getResultOfMonday());
                resDTO.setResultOfTuesday(e.getResultOfTuesday());
                resDTO.setResultOfWednesday(e.getResultOfWednesday());
                resDTO.setResultOfThursday(e.getResultOfThursday());
                resDTO.setResultOfFriday(e.getResultOfFriday());
                resDTO.setSubsidRuleId(subsidRuleId);
                return resDTO;
            }).collect(Collectors.toList());
            PageUtil.setOneAsFirstPageNo();
            int start = PageUtil.getStart(reqDTO.getPageNum(), reqDTO.getPageSize());
            int end = PageUtil.getEnd(reqDTO.getPageNum(), reqDTO.getPageSize());
            List<SubsidStuAttendanceResultsResDTO> subList = resDTOList.subList(start, Math.min(end, resDTOList.size()));
            page.setTotal(resDTOList.size());
            PaginationRes<SubsidStuAttendanceResultsResDTO> res = PaginationRes.of(subList, page);
            res.getPaging().setPageNum(reqDTO.getPageNum());
            return res;
        }

        SubsidStuAttendanceResultsDO queryDO = new SubsidStuAttendanceResultsDO();
        queryDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        queryDO.setPersonNo(reqDTO.getPersonNo());
        List<SubsidStuAttendanceResultsDO> selectList = subsidStuAttendanceResultsMapper.select(queryDO);
        List<SubsidStuAttendanceResultsResDTO> subsidStuAttendanceResultsResDTOS = JSON.parseArray(JSON.toJSONString(selectList), SubsidStuAttendanceResultsResDTO.class);
        return PaginationRes.of(subsidStuAttendanceResultsResDTOS, page);
    }

    @Override
    public void exportExcel(SubsidStuAttendanceResultsReqDTO reqDTO, HttpServletResponse resp) {
        SubsidStuAttendanceResultsDO queryDO = new SubsidStuAttendanceResultsDO();
        queryDO.setPersonNo(reqDTO.getPersonNo());
        queryDO.setSubsidRuleId(reqDTO.getSubsidRuleId());
        List<SubsidStuAttendanceResultsDO> selectList = subsidStuAttendanceResultsMapper.select(queryDO);
        int index = 1;
        List<SubsidStuAttendanceResultsExcelDTO> resultsDOS = new ArrayList<>();
        for (SubsidStuAttendanceResultsDO resultDO : selectList) {
            SubsidStuAttendanceResultsExcelDTO build = SubsidStuAttendanceResultsExcelDTO.builder()
                    .id(index++)
                    .attendanceRangeDate(resultDO.getAttendanceStartDate() + "-" + resultDO.getAttendanceEndDate())
                    .result(resultDO.getResult() == 0 ? "正常" : "缺勤")
                    .taskName(resultDO.getTaskName())
                    .attendanceType(resultDO.getAttendanceType() == 0 ? "抽查考勤" : "日常考勤")
                    .resultOfFriday(getResutlsName(resultDO.getResultOfFriday()))
                    .resultOfMonday(getResutlsName(resultDO.getResultOfMonday()))
                    .resultOfThursday(getResutlsName(resultDO.getResultOfThursday()))
                    .resultOfTuesday(getResutlsName(resultDO.getResultOfTuesday()))
                    .resultOfWednesday(getResutlsName(resultDO.getResultOfWednesday())).build();
            resultsDOS.add(build);
        }
        String excelName = "资助比对考勤详情";
        if (selectList.size() > 0) {
            excelName = selectList.get(0).getPersonName() + excelName;
        }
        ExcelUtil.exportExcel(resp, SubsidStuAttendanceResultsExcelDTO.class, resultsDOS, excelName);
    }

    @Override
    public void generateCallTheRollAttendanceResults(SubsidBaseReqDTO reqDTO) {

    }

    private String getResutlsName(Short result) {
        if (null == result) {
            return null;
        }
        String nameByType = AttendanceResult.getNameByType(result);

        return nameByType;
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        AtomicInteger pageIndex = new AtomicInteger(1);
        List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();
        int i = 0;
        while (++i <= 10) {
            System.out.println(i);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                log.info("pageIndex.get():{}", pageIndex.getAndIncrement());
            }, executorService);
            completableFutureList.add(future);
        }
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();
    }

}
