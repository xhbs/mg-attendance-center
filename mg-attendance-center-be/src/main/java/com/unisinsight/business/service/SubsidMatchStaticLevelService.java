/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticLevelResDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/09/01 15:06:32
 * @since 1.0
 */
public interface SubsidMatchStaticLevelService  {


    PaginationRes<SubsidMatchStaticLevelResDTO> getPage(SubsidMatchStaticLevelReqDTO reqDTO);

    /**
      *@description 手动分页查询
      *@param
      *@return
      *@date    2021/9/26 16:13
      */
    PaginationRes<SubsidMatchStaticLevelResDTO> getPageByHandle(SubsidMatchStaticLevelReqDTO reqDTO);

    void generateSubsidLevelResultsNew(SubsidBaseReqDTO reqDTO);

    void generateSubsidLevelResults(SubsidBaseReqDTO reqDTO);

    void exportExcel(Integer subsidRuleId, String orgParentIndex , HttpServletResponse resp) throws IOException;

}
