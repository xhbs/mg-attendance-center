package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.request.PracticeAddReqDTO;
import com.unisinsight.business.dto.request.PracticeRecordQueryReqDTO;
import com.unisinsight.business.dto.response.PracticeRecordDetailDTO;
import com.unisinsight.business.dto.response.PracticeRecordListDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 实习管理服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
public interface PracticeRecordService {

    /**
     * 新增
     *
     * @param req 入参
     * @return 记录ID
     */
    Integer add(PracticeAddReqDTO req);

    /**
     * 修改
     *
     * @param id  主键
     * @param req 入参
     */
    void update(Integer id, PracticeAddReqDTO req);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 实习记录详情
     */
    PracticeRecordDetailDTO get(Integer id);

    /**
     * 分页查询
     *
     * @param req 查询参数
     * @return 实习记录列表、分页信息
     */
    PaginationRes<PracticeRecordListDTO> list(PracticeRecordQueryReqDTO req);

    /**
     * 批量上报
     *
     * @param ids 主键列表
     */
    void report(List<Integer> ids);

    /**
     * 批量审核
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

    PracticeRecordDetailDTO selectDetailByPersonNo(String personNo, LocalDate date);
}
