package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.SpotTaskResultCountBO;
import com.unisinsight.business.bo.TaskResultCountBO;
import com.unisinsight.business.dto.request.TaskResultListReqDTO;
import com.unisinsight.business.dto.response.SpotCheckTaskRecordDTO;
import com.unisinsight.business.dto.response.TaskResultListResDTO;
import com.unisinsight.business.model.TaskResultDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 抽查任务结果表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
public interface TaskResultMapper extends Mapper<TaskResultDO> {

    /**
     * 批量保存
     */
    void batchSave(@Param("results") List<TaskResultDO> results);

    /**
     * 分页查询
     */
    List<TaskResultListResDTO> list(TaskResultListReqDTO param);

    /**
     * 查询考勤详情根据人员List，时间范围查询
     */
    List<SpotCheckTaskRecordDTO> findDetailList(@Param("personNos") List<String> personNos,
                                                @Param("attendanceDateSt") LocalDate attendanceDateSt,
                                                @Param("attendanceDateEd") LocalDate attendanceDateEd,
                                                @Param("rule") Integer rule);

    /**
     * 统计在校、请假、实习数量
     */
    TaskResultCountBO countAtDate(@Param("taskId") Integer taskId,
                                  @Param("personNos") List<String> personNos,
                                  @Param("attendanceDate") LocalDate attendanceDate);

    /**
     * 查询考勤状态
     */
    TaskResultDO findResult(@Param("personNo") String personNo, @Param("taskId") Integer taskId
            , @Param("day") LocalDate day, @Param("result") Integer result);

    /**
     * 根据任务id和组织id查询考勤详情
     */
    List<TaskResultDO> selectByTaskIdAndOrg(Integer taskId, String orgIndex);

    SpotTaskResultCountBO selectSpotPersonCount(int taskId, String orgIndex);
}