package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.DailyAttendanceExcludeDateDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateAddReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateQueryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateUpdateReqDTO;

import java.util.List;

/**
 * 日常考勤排除日期配置服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
public interface DailyAttendanceExcludeDateService {

    /**
     * 添加
     */
    void add(DailyAttendanceExcludeDateAddReqDTO req);

    /**
     * 更新
     */
    void update(DailyAttendanceExcludeDateUpdateReqDTO req);

    /**
     * 分页查询
     */
    PaginationRes<DailyAttendanceExcludeDateDTO> query(DailyAttendanceExcludeDateQueryReqDTO req);

    /**
     * 批量删除
     */
    void batchDelete(List<Integer> ids);
}
