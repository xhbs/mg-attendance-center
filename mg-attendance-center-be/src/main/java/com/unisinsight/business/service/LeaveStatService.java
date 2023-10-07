package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.LeaveStatListReqDTO;
import com.unisinsight.business.dto.response.LeaveStatListResDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * 请假统计服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/23
 */
public interface LeaveStatService {

    /**
     * 分页查询
     */
    PaginationRes<LeaveStatListResDTO> list(LeaveStatListReqDTO req);

    /**
     * 导出
     */
    void export(LeaveStatListReqDTO req, HttpServletResponse resp);
}
