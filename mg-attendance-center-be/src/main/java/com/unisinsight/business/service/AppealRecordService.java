package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.AppealAddReqDTO;
import com.unisinsight.business.dto.request.AppealRecordQueryReqDTO;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.response.AppealRecordDetailDTO;
import com.unisinsight.business.dto.response.AppealRecordListDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 考勤申诉记录服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11 11:23:53
 * @since 1.0
 */
public interface AppealRecordService {

    /**
     * 新增
     *
     * @param req 入参
     */
    void add(AppealAddReqDTO req);

    /**
     * 修改
     *
     * @param id  主键
     * @param req 入参
     */
    void update(Integer id, AppealAddReqDTO req);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 申诉记录详情
     */
    AppealRecordDetailDTO get(Integer id);

    /**
     * 分页查询
     *
     * @param req 查询入参
     * @return 申诉记录列表、分页信息
     */
    PaginationRes<AppealRecordListDTO> list(AppealRecordQueryReqDTO req);

    /**
     * 批量申诉
     *
     * @param ids 主键列表
     */
    void appeal(List<Integer> ids);

    /**
     * 批量处理
     *
     * @param req 审核参数
     */
    void approval(ApprovalReqDTO req);

    /**
     * 批量删除
     *
     * @param ids 主键列表
     */
    void delete(List<Integer> ids);

    /**
     * 导出
     *
     * @param req  查询参数
     * @param resp {@link HttpServletResponse}
     */
    void export(AppealRecordQueryReqDTO req, HttpServletResponse resp);
}
