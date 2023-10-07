package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.LeaveInfoDTO;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.request.LeaveAddReqDTO;
import com.unisinsight.business.dto.request.LeaveRecordQueryReqDTO;
import com.unisinsight.business.dto.request.LeaveUpdateReqDTO;
import com.unisinsight.business.dto.response.LeaveRecordDetailDTO;
import com.unisinsight.business.dto.response.LeaveRecordListDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 请假管理服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
public interface LeaveRecordService {

    /**
     * 批量新增
     *
     * @param req 入参
     * @return 记录ID列表
     */
    List<Integer> add(LeaveAddReqDTO req);

    /**
     * 修改
     *
     * @param id  主键
     * @param req 入参
     */
    void update(Integer id, LeaveUpdateReqDTO req);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 请假记录详情
     */
    LeaveRecordDetailDTO get(Integer id);

    /**
     * 分页查询
     *
     * @param req 查询参数
     * @return 请假记录列表、分页信息
     */
    PaginationRes<LeaveRecordListDTO> list(LeaveRecordQueryReqDTO req);

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

    LeaveInfoDTO selectByPersonNoAndDate(String personNo, LocalDate date);
}
