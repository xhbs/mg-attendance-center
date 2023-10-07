package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.PracticePersonQueryReqDTO;
import com.unisinsight.business.dto.response.PracticePersonDetailDTO;
import com.unisinsight.business.dto.response.PracticePersonListDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * 实习人员统计服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
public interface PracticePersonService {

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 请假记录详情
     */
    PracticePersonDetailDTO get(Integer id);

    /**
     * 分页查询
     *
     * @param req 查询参数
     * @return 请假记录列表、分页信息
     */
    PaginationRes<PracticePersonListDTO> list(PracticePersonQueryReqDTO req);

    /**
     * 导出
     *
     * @param req  查询参数
     * @param resp {@link HttpServletResponse}
     */
    void export(PracticePersonQueryReqDTO req, HttpServletResponse resp);
}
