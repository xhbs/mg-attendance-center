package com.unisinsight.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.DailyAttendanceStaticLevelBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.bo.StaticDateBO;
import com.unisinsight.business.common.utils.TransferDataUtils;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.request.DailyAttendanceHighLvlStaticDTO;
import com.unisinsight.business.dto.request.DailyAttendanceHighLvlStaticExcelDTO;
import com.unisinsight.business.dto.request.DailyAttendanceHighLvlStaticQueryReqDTO;
import com.unisinsight.business.mapper.DailyAttendanceStaticLevelMapper;
import com.unisinsight.business.model.DailyAttendanceStaticLevelDO;
import com.unisinsight.business.service.IDailyAttendanceStaticLevelServcie;
import com.unisinsight.framework.common.exception.BaseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanggang
 * @version 1.0
 *
 * @email tang.gang@inisinsight.com
 * @date 2021/8/17 10:35
 **/
@Service
public class DailyAttendanceStaticLevelServcieImpl implements IDailyAttendanceStaticLevelServcie {

    @Autowired
    private DailyAttendanceStaticLevelMapper dailyAttendanceStaticLevelMapper;


    @Override
    public List<DailyAttendanceStaticLevelBO> findDailyAttendanceStaticHighLvlList(DailyAttendanceStaticLevelBO queryBO) {
        List<DailyAttendanceStaticLevelBO> dailyAttendanceStaticLevelBOS = dailyAttendanceStaticLevelMapper.selectStaticLevelList(queryBO);
        return dailyAttendanceStaticLevelBOS;
    }

    @Override
    public void insertList(List<DailyAttendanceStaticLevelDO> staticLevelBaseDOList) {
        List<DailyAttendanceStaticLevelDO> dailyAttendanceStaticLevel2DOS = JSON.parseArray(JSON.toJSONString(staticLevelBaseDOList), DailyAttendanceStaticLevelDO.class);
        dailyAttendanceStaticLevelMapper.insertList(   dailyAttendanceStaticLevel2DOS);
    }

    @Override
    public boolean checkWeekExist(DailyAttendanceStaticLevelBO queryBO) {
        Integer integer = dailyAttendanceStaticLevelMapper.checkWeekExist(queryBO);
        if(integer == null){
            return false;
        }
        if(integer == 1){
            return true;
        }
        return false;
    }

    @Override
    public PaginationRes<DailyAttendanceHighLvlStaticDTO> getPage(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO) {
        Page page = PageHelper.startPage(reqDTO.getPageNum(), reqDTO.getPageSize());
        DailyAttendanceStaticLevelBO queryBO = new DailyAttendanceStaticLevelBO();
        BeanUtils.copyProperties(reqDTO,queryBO);
        StaticDateBO staticDateBO = new StaticDateBO();
        BeanUtils.copyProperties(reqDTO,staticDateBO);
        if(StringUtils.isEmpty(reqDTO.getYearMonth())){//如果没传年月，默认是查询学期记录中最大年月
            reqDTO.setYearMonth(dailyAttendanceStaticLevelMapper.getMaxYearMonth(reqDTO.getSchoolYear(),reqDTO.getSchoolTerm()));
        }
        List<DailyAttendanceStaticLevelBO> dailyAttendanceStaticLevelBOS = dailyAttendanceStaticLevelMapper.selectStaticLevelList(queryBO);
        List<DailyAttendanceHighLvlStaticDTO> dailyAttendanceHighLvlStaticDTOS = JSON.parseArray(JSON.toJSONString(dailyAttendanceStaticLevelBOS), DailyAttendanceHighLvlStaticDTO.class);
        return PaginationRes.of(dailyAttendanceHighLvlStaticDTOS, page);
    }

    @Override
    public PaginationRes<DailyAttendanceHighLvlStaticDTO> getPageHandle(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO) {
        if(reqDTO.getPageNum() == 1){
            reqDTO.setOffset(reqDTO.getPageSize()* (reqDTO.getPageNum()-1));
            reqDTO.setLimit(reqDTO.getPageSize()-1);
        }else{
            reqDTO.setOffset(reqDTO.getPageSize()* (reqDTO.getPageNum()-1)-1);
            reqDTO.setLimit(reqDTO.getPageSize());
        }
        if(StringUtils.isEmpty(reqDTO.getYearMonth())){//如果没传年月，默认是查询学期记录中最大年月
            reqDTO.setYearMonth(dailyAttendanceStaticLevelMapper.getMaxYearMonth(reqDTO.getSchoolYear(),reqDTO.getSchoolTerm()));
        }
        int total = dailyAttendanceStaticLevelMapper.selectTotal(reqDTO);
        List<DailyAttendanceStaticLevelBO> dailyAttendanceStaticLevelBOS = dailyAttendanceStaticLevelMapper.selectStaticLevelListByHandle(reqDTO);
        List<DailyAttendanceHighLvlStaticDTO> sonList = JSON.parseArray(JSON.toJSONString(dailyAttendanceStaticLevelBOS), DailyAttendanceHighLvlStaticDTO.class);
        if(reqDTO.getPageNum() == 1 && StringUtils.isEmpty(reqDTO.getOrgName())){//第一页增加合计
            DailyAttendanceStaticLevelBO queryBO = new DailyAttendanceStaticLevelBO();
            queryBO.setSchoolYear(reqDTO.getSchoolYear());
            queryBO.setSchoolTerm(reqDTO.getSchoolTerm());
            queryBO.setYearMonth(reqDTO.getYearMonth());
            queryBO.setOrgIndex(reqDTO.getOrgParentIndex());
            List<DailyAttendanceStaticLevelBO> parentStaticBOList = dailyAttendanceStaticLevelMapper.selectStaticLevelList(queryBO);
            if(parentStaticBOList.size() <= 0){
                return PaginationRes.of(null,new Page());
            }
            parentStaticBOList.get(0).setOrgName("合计");
            parentStaticBOList.get(0).setSubType(null);
            List<DailyAttendanceHighLvlStaticDTO> restList = JSON.parseArray(JSON.toJSONString(parentStaticBOList), DailyAttendanceHighLvlStaticDTO.class);
            restList.addAll(sonList);
            return PaginationRes.of(restList,reqDTO.getPageNum(),reqDTO.getPageSize(),++total);
        }

        return PaginationRes.of(sonList,reqDTO.getPageNum(),reqDTO.getPageSize(),total);
    }

    @Override
    public void batchUpdate(List<DailyAttendanceStaticLevelDO> results) {
        dailyAttendanceStaticLevelMapper.batchUpdate(results);
    }

    @Override
    public void exportLvlExcel(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO, HttpServletResponse httpServletResponse) {
        int total = dailyAttendanceStaticLevelMapper.selectTotal(reqDTO);
        if (total<= 0) {
            throw new InvalidParameterException("导出记录为空");
        }
        reqDTO.setPageSize(++total);
        PaginationRes<DailyAttendanceHighLvlStaticDTO> page = getPageHandle(reqDTO);
        List<DailyAttendanceHighLvlStaticDTO> datas = page.getData();
        List<DailyAttendanceHighLvlStaticExcelDTO> exceList = new ArrayList<>();
        for (int i = 0; i <datas.size() ; i++) {
            DailyAttendanceHighLvlStaticDTO item = datas.get(i);
            DailyAttendanceHighLvlStaticExcelDTO excelDTO = new DailyAttendanceHighLvlStaticExcelDTO();
            excelDTO.setId(i+1);
            excelDTO.setOrgName(item.getOrgName());
            excelDTO.setSchoolYear(item.getSchoolYear());
            excelDTO.setSchoolTerm("0".equals(item.getSchoolTerm())? "春季":"秋季");
            excelDTO.setYearMonth(item.getYearMonth());
            excelDTO.setCheckWeek(item.getCheckWeek());
            excelDTO.setCheckType("周考勤");
            excelDTO.setRegistStudentNum(item.getRegistStudentNum());
            excelDTO.setStudentNum(item.getStudentNum());
            excelDTO.setRange1(item.getRange1());
            excelDTO.setRange2(item.getRange2());
            excelDTO.setRange3(item.getRange3());
            excelDTO.setRange4(item.getRange4());
            excelDTO.setRange5(item.getRange5());
            excelDTO.setRange6(item.getRange6());
            exceList.add(excelDTO);
        }
        ExcelUtil.exportExcel(httpServletResponse, DailyAttendanceHighLvlStaticExcelDTO.class, exceList,"日常考勤统计");

    }
}
