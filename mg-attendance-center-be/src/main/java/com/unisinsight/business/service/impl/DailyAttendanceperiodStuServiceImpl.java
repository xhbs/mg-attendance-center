package com.unisinsight.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.DailyAttendancePeriodStuBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.bo.StaticDateBO;
import com.unisinsight.business.common.utils.TransferDataUtils;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.request.DailyAttendanceStuStaticDTO;
import com.unisinsight.business.dto.request.DailyAttendanceStuStaticExcelDTO;
import com.unisinsight.business.dto.request.DailyAttendanceStuStaticQueryReqDTO;
import com.unisinsight.business.mapper.DailyAttendancePeriodStuMapper;
import com.unisinsight.business.mapper.DailyAttendanceStaticLevelMapper;
import com.unisinsight.business.model.DailyAttendancePeriodStuDO;
import com.unisinsight.business.service.IDailyAttendanceperiodStuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @date 2021/8/17 21:38
 **/
@Service
public class DailyAttendanceperiodStuServiceImpl implements IDailyAttendanceperiodStuService {
    @Autowired
    private DailyAttendancePeriodStuMapper dailyAttendancePeriodStuMapper;
    @Value("${excel-export.max-size:10000}")
    protected int excelMaxExportRows;
    @Autowired
    private TransferDataUtils transferDataUtils;
    @Autowired
    private DailyAttendanceStaticLevelMapper dailyAttendanceStaticLevelMapper;

    @Override
    public PaginationRes<DailyAttendanceStuStaticDTO> getPage(DailyAttendanceStuStaticQueryReqDTO reqDTO) {
        if(StringUtils.isEmpty(reqDTO.getYearMonth())){//如果没传年月，默认是查询学期记录中最大年月
            reqDTO.setYearMonth(dailyAttendanceStaticLevelMapper.getMaxYearMonth(reqDTO.getSchoolYear(),reqDTO.getSchoolTerm()));
        }
        DailyAttendancePeriodStuBO queryBO = new DailyAttendancePeriodStuBO();
        BeanUtils.copyProperties(reqDTO,queryBO);
        StaticDateBO staticDateBO = new StaticDateBO();
        BeanUtils.copyProperties(reqDTO,staticDateBO);
        queryBO.setCreateTimeSt(transferDataUtils.getCreateDateSt(staticDateBO));
        queryBO.setCreateTimeEd(transferDataUtils.getCreateDateEd(staticDateBO));
        //分页查询
        Page page = PageHelper.startPage(reqDTO.getPageNum(), reqDTO.getPageSize());
        List<DailyAttendancePeriodStuDO> stuAttendanceList = dailyAttendancePeriodStuMapper.findStuAttendanceList(queryBO);
        List<DailyAttendanceStuStaticDTO> dailyAttendancePeriodStuDTOS = JSON.parseArray(JSON.toJSONString(stuAttendanceList), DailyAttendanceStuStaticDTO.class);
        return PaginationRes.of(dailyAttendancePeriodStuDTOS, page);
    }

    @Override
    public void exportExcel(DailyAttendanceStuStaticQueryReqDTO reqDTO, HttpServletResponse resp) {
        if(StringUtils.isEmpty(reqDTO.getYearMonth())){//如果没传年月，默认是查询学期记录中最大年月
            reqDTO.setYearMonth(dailyAttendanceStaticLevelMapper.getMaxYearMonth(reqDTO.getSchoolYear(),reqDTO.getSchoolTerm()));
        }
        DailyAttendancePeriodStuBO queryBO = new DailyAttendancePeriodStuBO();
        BeanUtils.copyProperties(reqDTO,queryBO);
        StaticDateBO staticDateBO = new StaticDateBO();
        BeanUtils.copyProperties(reqDTO,staticDateBO);
        queryBO.setCreateTimeSt(transferDataUtils.getCreateDateSt(staticDateBO));
        queryBO.setCreateTimeEd(transferDataUtils.getCreateDateEd(staticDateBO));
        int total = dailyAttendancePeriodStuMapper.countByConditions(queryBO);
        if (total == 0) {
            throw new InvalidParameterException("导出记录为空");
        }
        PageHelper.startPage(1,total > excelMaxExportRows?excelMaxExportRows : total);
        List<DailyAttendancePeriodStuDO> stuAttendanceList = dailyAttendancePeriodStuMapper.findStuAttendanceList(queryBO);
        List<DailyAttendanceStuStaticExcelDTO> excelDTOList = new ArrayList();
        for (int i = 0; i < stuAttendanceList.size(); i++) {
            DailyAttendancePeriodStuDO item = stuAttendanceList.get(i);
            DailyAttendanceStuStaticExcelDTO excelDTO = DailyAttendanceStuStaticExcelDTO.builder()
                    .id(i+1)
                    .personName(item.getPersonName())
                    .personNo(item.getPersonNo())
                    .schoolYear(item.getSchoolYear())
                    .schoolTerm("0".equals(item.getSchoolTerm())? "春季":"秋季")
                    .yearMonth(item.getYearMonth())
                    .checkWeek(item.getCheckWeek())
                    .checkType("周考勤")
                    .normalWeeks(Integer.valueOf(item.getNormalWeeks()+"") )
                    .absentWeeks(Integer.valueOf(item.getAbsentWeeks()+""))
                    .build();
            excelDTOList.add(excelDTO);
        }
        ExcelUtil.exportExcel(resp, DailyAttendanceStuStaticExcelDTO.class, excelDTOList,"日常考勤统计");
    }
}
