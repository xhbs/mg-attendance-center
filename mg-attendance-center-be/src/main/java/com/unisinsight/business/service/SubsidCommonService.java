/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.response.SubsidBaseResDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticLevelResDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;


/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 15:06:32
 * @since 1.0
 */
public interface SubsidCommonService {

    @Transactional(rollbackFor = Exception.class)
    SubsidBaseResDTO autoSubsidResults(SubsidBaseReqDTO reqDTO);

    SubsidBaseResDTO generateSubsidResults(SubsidBaseReqDTO reqDTO);

    Results preCheckExportExcel(Integer subsidRuleId, String orgIndex );

    void exportExcel(Integer subsidRuleId, String orgIndex , HttpServletResponse resp);


}
