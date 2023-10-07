package com.unisinsight.business.service;

import com.unisinsight.business.bo.FindStuWeekResultParamBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.enums.AdjustModeEnum;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.dto.DailyAttendanceResultDTO;
import com.unisinsight.business.dto.StuWeekResultCountDTO;
import com.unisinsight.business.dto.request.AtsHistoryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceDetailQueryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceDetailReqDTO;
import com.unisinsight.business.dto.response.AtsHistoryResDTO;
import com.unisinsight.business.dto.response.DailyAttendanceDetailResDTO;
import com.unisinsight.business.model.DailyAttendanceResultDO;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

/**
 * 日常考勤明细查询服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
public interface DailyAttendanceResultService {

    /**
     * 批量保存
     */
    void batchSave(LocalDate date, List<DailyAttendanceResultDO> results);

    /**
     * 更新
     */
    void update(DailyAttendanceResultDO record, AttendanceResult result);

    /**
     * 调整非正常的考勤记录
     */
    void updateResults(String personNo, LocalDate startDate, LocalDate endDate, AttendanceResult result,
                       String comment, AdjustModeEnum mode);

    /**
     * 调整考勤记录，如果当天没有记录则生成记录
     */
    void updateOrCreateResult(String personNo, LocalDate attendanceDate, AttendanceResult result,
                              String comment, AdjustModeEnum mode);

    /**
     * 分页查询
     */
    PaginationRes<DailyAttendanceResultDTO> query(DailyAttendanceDetailQueryReqDTO req);

    /**
     * 查询详情
     */
    DailyAttendanceDetailResDTO getDetails(@Validated DailyAttendanceDetailReqDTO req);

    /**
     * 根据人员查询历史考勤
     */
    AtsHistoryResDTO getHistoryByPerson(AtsHistoryReqDTO req);


    List<StuWeekResultCountDTO> findStuWeekResultList(FindStuWeekResultParamBO param);
}
