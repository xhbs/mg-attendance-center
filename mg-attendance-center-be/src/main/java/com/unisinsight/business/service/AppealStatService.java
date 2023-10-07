package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.AppealPersonListReqDTO;
import com.unisinsight.business.dto.request.AppealStatListReqDTO;
import com.unisinsight.business.dto.response.AppealPersonListResDTO;
import com.unisinsight.business.dto.response.AppealStatListResDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * 申诉统计服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/29
 */
public interface AppealStatService {

    /**
     * 分页查询 - 按组织统计
     */
    PaginationRes<AppealStatListResDTO> statByOrg(AppealStatListReqDTO req);

    /**
     * 导出 - 按组织统计
     */
    void exportByOrg(AppealStatListReqDTO req, HttpServletResponse resp);

    /**
     * 分页查询 - 按学生统计
     */
    PaginationRes<AppealPersonListResDTO> statByPerson(AppealPersonListReqDTO req);

    /**
     * 导出 - 按学生统计
     */
    void exportByPerson(AppealPersonListReqDTO req, HttpServletResponse resp);
}
