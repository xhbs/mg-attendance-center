/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.unisinsight.business.bo.*;
import com.unisinsight.business.common.utils.DateRangeCalcUtil;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.SubsidStuListStipendExcelDTO;
import com.unisinsight.business.dto.SubsidStuListWaiveExcelDTO;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.request.SubsidRosterInfo;
import com.unisinsight.business.dto.response.SubsidBaseResDTO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.SubsidCompareRuleDO;
import com.unisinsight.business.model.SubsidStuListDO;
import com.unisinsight.business.service.*;
import com.unisinsight.framework.common.exception.BaseErrorCode;
import com.unisinsight.framework.common.exception.BaseException;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Struct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.unisinsight.business.common.constants.Constants.IMPORT_ROW_MAX;


/**
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 15:06:32
 * @since 1.0
 */
@Service
@Slf4j
public class SubsidCommonServiceImpl implements SubsidCommonService {
    @Autowired
    private SubsidCompareRuleService subsidCompareRuleService;
    @Autowired
    private SubsidMatchStaticLevelService subsidMatchStaticLevelService;
    @Autowired
    private SubsidMatchStaticStuService subsidMatchStaticStuService;
    @Autowired
    private SubsidStuAttendanceResultsService subsidStuAttendanceResultsService;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private SubsidStuListService subsidStuListService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubsidBaseResDTO generateSubsidResults(SubsidBaseReqDTO reqDTO) {
        //学期学年不为空,计算一个学期的考勤率
//        if (StrUtil.isNotBlank(reqDTO.getSchoolYear()) && StrUtil.isNotBlank(reqDTO.getSchoolTerm())) {
//            if (!"春季".equals(reqDTO.getSchoolTerm()) && !"秋季".equals(reqDTO.getSchoolTerm())) {
//                throw new BaseException("学期选择错误!");
//            }
//            DateRangeBO calc = DateRangeCalcUtil.calc(reqDTO.getSchoolYear(), reqDTO.getSchoolTerm(), null);
//            reqDTO.setChkDateSt(calc.getStartDate());
//            reqDTO.setChkDateEd(calc.getEndDate());
//        }
        //默认使用点名考勤
        if (ObjectUtils.isEmpty(reqDTO.getRule())) {
            reqDTO.setRule(1);
        }
        if (reqDTO.getSubsidType() == 0) {//自动比对，设置subListIndex
            String subListIndex = reqDTO.getSubsidRosterInfo().getRosterDate() + reqDTO.getSubsidRosterInfo().getPrjType();
            reqDTO.setSubListIndex(subListIndex);
        }
        if (reqDTO.getRule() != 1) {//自动比对，设置subListIndex
            //原来方法太慢,自动资助走新逻辑
            return autoSubsidResults(reqDTO);
        }
        SubsidBaseResDTO restsult = this.preCheck(reqDTO);
        if (null != restsult) {
            return restsult;
        }
        String key = checkKey(reqDTO, "callTheRollSubsidResults");
        SubsidBaseResDTO subsidBaseResDTO = subsidCompareRuleService.generateSubsidCompareRule(reqDTO);//生成比对规则记录
        LocalDateTime startTime = LocalDateTime.now();
        //生成考勤明细
        subsidStuAttendanceResultsService.generateSubsidStuAttendanceResults(reqDTO);//先生成考勤明细
        LocalDateTime genAttendanceTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, genAttendanceTime);
        log.info("生成考勤明细耗费时间------{}s", duration.getSeconds());
        subsidMatchStaticStuService.generateSubsidStuAttendanceResults(reqDTO);//汇总生成学生比对数据
        LocalDateTime genStuTime = LocalDateTime.now();
        Duration duration2 = Duration.between(genAttendanceTime, genStuTime);
        log.info("汇总生成学生比对数据耗费时间------{}s", duration2.getSeconds());
        subsidMatchStaticLevelService.generateSubsidLevelResults(reqDTO);//汇总生成节点数据
        LocalDateTime genLvlTime = LocalDateTime.now();
        Duration duration3 = Duration.between(genStuTime, genLvlTime);
        log.info("汇总生成节点数据数据耗费时间------{}s", duration3.getSeconds());
        stringRedisTemplate.delete(key);
        return subsidBaseResDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubsidBaseResDTO autoSubsidResults(SubsidBaseReqDTO reqDTO) {
        //默认使用点名考勤
        SubsidBaseResDTO restsult = this.preCheck(reqDTO);
        if (null != restsult) {
            return restsult;
        }
        String key = checkKey(reqDTO, "autoSubsidResults");
        SubsidBaseResDTO subsidBaseResDTO = subsidCompareRuleService.generateSubsidCompareRule(reqDTO);//生成比对规则记录
        LocalDateTime startTime = LocalDateTime.now();
        //生成考勤明细
        subsidMatchStaticStuService.generateSubsidStuAttendanceResultsNew(reqDTO);//汇总生成学生比对数据
        LocalDateTime genStuTime = LocalDateTime.now();
        Duration duration2 = Duration.between(startTime, genStuTime);
        log.info("汇总生成学生比对数据耗费时间------{}s", duration2.getSeconds());
        subsidMatchStaticLevelService.generateSubsidLevelResultsNew(reqDTO);//汇总生成节点数据
        LocalDateTime genLvlTime = LocalDateTime.now();
        Duration duration3 = Duration.between(genStuTime, genLvlTime);
        log.info("汇总生成节点数据数据耗费时间------{}s", duration3.getSeconds());
        stringRedisTemplate.delete(key);
        return subsidBaseResDTO;
    }

    private String checkKey(SubsidBaseReqDTO reqDTO,String keyPre){
        //redis　中有缓存，任务没执行完，提示等待，否则放行
        String data = JSON.toJSONString(reqDTO);
        log.info("资助参数：" + data);
        String key = keyPre+"::" + MD5.create().digestHex(data);
        log.info("资助参数key：" + key);
        Boolean aBoolean = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(aBoolean)) {
            throw BaseException.of("512", "资助比对计算中,请等待2-5分钟再次查询");
        } else {
            stringRedisTemplate.opsForValue().set(key, "1", 10, TimeUnit.MINUTES);
        }
        return key;
    }

    @Override
    public Results preCheckExportExcel(Integer subsidRuleId, String orgIndex) {
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleService.findById(subsidRuleId);
        List<String> orgIndexList = new ArrayList<>();
        orgIndexList.add(orgIndex);
        List<String> orgParentIndexs = organizationMapper.selectLowerOrgIndex(orgIndexList, (short) 4);
        QuerySubsidStuBO querySubsidStuBO = new QuerySubsidStuBO();
        querySubsidStuBO.setSubsidRuleId(subsidRuleId);
        querySubsidStuBO.setOrgParentIndexs(orgParentIndexs);
        querySubsidStuBO.setSubListIndex(subsidCompareRuleDO.getSubListIndex());
        PaginationReq paginationReq = new PaginationReq();
        paginationReq.setPageNum(1);
        paginationReq.setPageSize(IMPORT_ROW_MAX);
        PaginationRes<SubsidStuListDO> subsidStuListDOPage = subsidStuListService.getSubsidStuExcelPage(querySubsidStuBO, paginationReq);
        if (subsidStuListDOPage.getPaging().getTotal() <= 0) {//无导出数据
            return Results.error(BaseErrorCode.RESULT_EMPTY_ERROR.getErrorCode(), "没有比对通过资助学生名单");
        }
        return Results.success();
    }


    @Override
    public void exportExcel(Integer subsidRuleId, String orgIndex, HttpServletResponse resp) {
        SubsidCompareRuleDO subsidCompareRuleDO = subsidCompareRuleService.findById(subsidRuleId);
        List<String> orgIndexList = new ArrayList<>();
        orgIndexList.add(orgIndex);
        //查询到校级
        List<String> orgIndexs = organizationMapper.selectLowerOrgIndex(orgIndexList, (short) 4);
        QuerySubsidStuBO querySubsidStuBO = new QuerySubsidStuBO();
        querySubsidStuBO.setSubsidRuleId(subsidRuleId);
        querySubsidStuBO.setOrgParentIndexs(orgIndexs);
        querySubsidStuBO.setSubListIndex(subsidCompareRuleDO.getSubListIndex());
        PaginationReq paginationReq = new PaginationReq();
        paginationReq.setPageNum(1);
        paginationReq.setPageSize(IMPORT_ROW_MAX);
        PaginationRes<SubsidStuListDO> subsidStuListDOPage = subsidStuListService.getSubsidStuExcelPage(querySubsidStuBO, paginationReq);
        short prjType;
        List<SubsidStuListDO> datas = subsidStuListDOPage.getData();
        if (0 == subsidCompareRuleDO.getSubsidType()) {
            //SubListIndex生成规则：日期（yyyy-mm）+项目类型+学校审核名单
            prjType = Short.valueOf(subsidCompareRuleDO.getSubListIndex().substring(7, 8));
        } else {
            //SubListIndex生成规则：项目类型+随机字符串
            prjType = Short.valueOf(subsidCompareRuleDO.getSubListIndex().substring(0, 1));
        }
        if (prjType == 0) {
            List<SubsidStuListWaiveExcelDTO> subsidStuListWaiveExcelDTOS = JSON.parseArray(JSON.toJSONString(datas), SubsidStuListWaiveExcelDTO.class);
            ExcelUtil.exportExcel(resp, SubsidStuListWaiveExcelDTO.class, subsidStuListWaiveExcelDTOS, "免学费资助名单");
        } else {
            List<SubsidStuListStipendExcelDTO> subsidStuListStipendExcelDTOS = JSON.parseArray(JSON.toJSONString(datas), SubsidStuListStipendExcelDTO.class);
            ExcelUtil.exportExcel(resp, SubsidStuListStipendExcelDTO.class, subsidStuListStipendExcelDTOS, "助学金资助名单");

        }
    }


    private SubsidBaseResDTO preCheck(SubsidBaseReqDTO reqDTO) {
        if (subsidCompareRuleService.existRecord(reqDTO)) {//如果已经有记录，则直接返回
            return new SubsidBaseResDTO(reqDTO.getSubsidRuleId());
        }
        PaginationReq paginationReq = new PaginationReq();
        paginationReq.setPageNum(1);
        paginationReq.setPageSize(1);
        PaginationRes<SubsidStuBO> page = subsidStuListService.getPage(reqDTO.getSubListIndex(), paginationReq);
        if (page.getPaging().getTotal() <= 0) {
            throw new BaseException("名单库记录不存在,或上传名单不匹配");
        }
        return null;
    }
}
