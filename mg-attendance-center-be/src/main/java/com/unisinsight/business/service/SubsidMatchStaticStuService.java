/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticStuExcelReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticStuReqDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticStuResDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 15:05:21
 * @since 1.0
 */
public interface SubsidMatchStaticStuService  {

    void generateSubsidStuAttendanceResults(SubsidBaseReqDTO reqDTO);

    PaginationRes<SubsidMatchStaticStuResDTO> getPage(SubsidMatchStaticStuReqDTO reqDTO);

    SubsidMatchStaticStuResDTO findById(Long id);

    void exportExcel(SubsidMatchStaticStuExcelReqDTO reqDTO , HttpServletResponse resp);

    void generateSubsidStuAttendanceResultsNew(SubsidBaseReqDTO reqDTO);
}
