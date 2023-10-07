package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.DailyAttendanceStuStaticDTO;
import com.unisinsight.business.dto.request.DailyAttendanceStuStaticQueryReqDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * @author tanggang
 * @version 1.0
 *
 * @email tang.gang@inisinsight.com
 * @date 2021/8/17 21:33
 **/
public interface IDailyAttendanceperiodStuService {


    PaginationRes<DailyAttendanceStuStaticDTO> getPage(DailyAttendanceStuStaticQueryReqDTO reqDTO);

    void exportExcel(DailyAttendanceStuStaticQueryReqDTO reqDTO, HttpServletResponse resp);
}
