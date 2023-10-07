package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.dto.response.*;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletResponse;

/**
 * 抽查考勤
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
public interface SpotCheckAttendanceService {

    /**
     * 抽查考勤统计查询
     */
    PaginationRes<TaskResultStatResDTO> statistics(TaskResultStatReqDTO req);

    /**
     * 抽查考勤明细查询
     */
    PaginationRes<TaskResultListResDTO> list(TaskResultListReqDTO req);

    /**
     * 抽查考勤详情查询
     */
    SpotAttendanceDetailResDTO getDetails(@Validated SpotAttendanceDetailReqDTO req);

    /**
     * 抽查考勤统计excel导出
     */
    void exportCountExcel(TaskResultStatExportReqDTO req, HttpServletResponse resp);

    /**
     * 抽查考勤明细excel导出
     */
    void exportListExcel(TaskResultListExportReqDTO req, HttpServletResponse resp);

    /**
     * 班主任抽查任务数量统计
     */
    SpotCheckNumDTO checkNum();

    /**
     * 查询班主任的考勤任务
     */
    PaginationRes<SpotCheckAttendListDTO> getTasksOfClasses(SpotCheckAttendListReqDTO req);

    /**
     * 查询当天有考勤任务的学生
     */
    PaginationRes<SpotCheckAttendDetailDTO> getHaveTaskStudents(SpotCheckAttendDetailReqDTO req);

    FeelAttendDetailDTO attendDetail(FeelAttendDetailReqDTO reqDTO);
}