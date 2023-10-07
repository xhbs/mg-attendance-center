/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.request.SubsidStuAttendanceResultsReqDTO;
import com.unisinsight.business.dto.response.SubsidStuAttendanceResultsResDTO;

import javax.servlet.http.HttpServletResponse;


/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:04:00
 * @since 1.0
 */
public interface SubsidStuAttendanceResultsService  {


    void generateSubsidStuAttendanceResults(SubsidBaseReqDTO reqDTO);

    /**
      *@description 分页查询
      *@param
      *@return
      *@date    2021/9/4 14:15
      */
    PaginationRes<SubsidStuAttendanceResultsResDTO> getPage(SubsidStuAttendanceResultsReqDTO reqDTO);

    void exportExcel(SubsidStuAttendanceResultsReqDTO reqDTO, HttpServletResponse resp);

    void generateCallTheRollAttendanceResults(SubsidBaseReqDTO reqDTO);
}
