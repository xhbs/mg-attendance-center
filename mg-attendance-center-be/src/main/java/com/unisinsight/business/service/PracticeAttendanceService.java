package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.PracticeAttendanceDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendanceListReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonListReqDTO;
import com.unisinsight.business.dto.response.PracticeAttendanceDetailDTO;
import com.unisinsight.business.dto.response.PracticeAttendanceListDTO;
import com.unisinsight.business.dto.response.PracticeAttendancePersonDetailsDTO;
import com.unisinsight.business.dto.response.PracticeAttendancePersonListDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * 实习点名服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
public interface PracticeAttendanceService {
    /**
     * 实习点名 - 分页查询
     */
    PaginationRes<PracticeAttendanceListDTO> list(PracticeAttendanceListReqDTO req);

    /**
     * 实习点名列表 - 导出
     */
    void exportList(PracticeAttendanceListReqDTO req, HttpServletResponse resp);

    /**
     * 考勤详情 - 分页查询
     */
    PaginationRes<PracticeAttendanceDetailDTO> details(PracticeAttendanceDetailsReqDTO req);

    /**
     * 考勤详情 - 导出
     */
    void exportDetails(PracticeAttendanceDetailsReqDTO req, HttpServletResponse resp);

    /**
     * H5 - 点名列表
     */
    PaginationRes<PracticeAttendancePersonListDTO> personList(PracticeAttendancePersonListReqDTO req);

    /**
     * H5 - 点名详情
     */
    PracticeAttendancePersonDetailsDTO personDetails(PracticeAttendancePersonDetailsReqDTO req);

    /**
     * H5 - 获取点名缺勤人数
     */
    Integer getAbsenceCount();

    /**
     * H5 - 标记缺勤已读
     */
    void markAsRead(Integer practicePersonId);
}
