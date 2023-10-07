package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.request.PracticeAttendanceDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendanceListReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonListReqDTO;
import com.unisinsight.business.dto.response.PracticeAttendanceDetailDTO;
import com.unisinsight.business.dto.response.PracticeAttendanceListDTO;
import com.unisinsight.business.dto.response.PracticeAttendancePersonDetailsDTO;
import com.unisinsight.business.dto.response.PracticeAttendancePersonListDTO;
import com.unisinsight.business.mapper.PracticePersonMapper;
import com.unisinsight.business.mapper.PracticeRecordMapper;
import com.unisinsight.business.service.PracticeAttendanceService;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 实习点名服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@Slf4j
@Service
public class PracticeAttendanceServiceImpl implements PracticeAttendanceService {

    @Resource
    private PracticeRecordMapper practiceRecordMapper;

    @Resource
    private PracticePersonMapper practicePersonMapper;

    @Override
    public PaginationRes<PracticeAttendanceListDTO> list(PracticeAttendanceListReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PracticeAttendanceListDTO> data = practiceRecordMapper.findAttendanceTasks(req);
        return PaginationRes.of(data, page);
    }

    @Override
    public void exportList(PracticeAttendanceListReqDTO req, HttpServletResponse resp) {
        User user = UserUtils.mustGetUser();
        List<PracticeAttendanceListDTO> data = practiceRecordMapper.findAttendanceTasks(req);
        ExcelUtil.export(resp, PracticeAttendanceListDTO.class, data, "实习点名");
        log.info("{} 导出了 {} 条实习点名记录，查询参数：{}", user.getUserCode(), data.size(), req);
    }

    @Override
    public PaginationRes<PracticeAttendanceDetailDTO> details(PracticeAttendanceDetailsReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PracticeAttendanceDetailDTO> data = practicePersonMapper.findAttendanceDetails(req);
        return PaginationRes.of(data, page);
    }

    @Override
    public void exportDetails(PracticeAttendanceDetailsReqDTO req, HttpServletResponse resp) {
        User user = UserUtils.mustGetUser();
        List<PracticeAttendanceDetailDTO> data = practicePersonMapper.findAttendanceDetails(req);
        ExcelUtil.export(resp, PracticeAttendanceDetailDTO.class, data, "实习点名考勤详情");
        log.info("{} 导出了 {} 条实习点名考勤详情记录，查询参数：{}", user.getUserCode(), data.size(), req);
    }

    @Override
    public PaginationRes<PracticeAttendancePersonListDTO> personList(PracticeAttendancePersonListReqDTO req) {
        req.setUserCode(UserUtils.mustGetUser().getUserCode());
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PracticeAttendancePersonListDTO> data = practicePersonMapper.findAttendancePersons(req);
        return PaginationRes.of(data, page);
    }

    @Override
    public PracticeAttendancePersonDetailsDTO personDetails(PracticeAttendancePersonDetailsReqDTO req) {
        return practicePersonMapper.findPersonAttendanceDetails(req);
    }

    @Override
    public Integer getAbsenceCount() {
        return practicePersonMapper.getAbsenceCount(UserUtils.mustGetUser().getUserCode());
    }

    @Override
    public void markAsRead(Integer practicePersonId) {
        practicePersonMapper.markAsRead(practicePersonId);
    }
}
