package com.unisinsight.business.mapper;

import com.unisinsight.business.dto.request.SpotCheckTaskListReqDTO;
import com.unisinsight.business.dto.response.SpotCheckAttendDetailDTO;
import com.unisinsight.business.dto.response.SpotCheckAttendListDTO;
import com.unisinsight.business.dto.response.SpotCheckNumDTO;
import com.unisinsight.business.dto.response.SpotCheckTaskDTO;
import com.unisinsight.business.model.SpotCheckTaskDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 抽查任务表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
public interface SpotCheckTaskMapper extends Mapper<SpotCheckTaskDO>, InsertUseGeneratedKeysMapper<SpotCheckTaskDO> {

    /**
     * 查询当天有抽查日期的任务
     */
    List<SpotCheckTaskDO> findByDate(LocalDate date);

    /**
     * 查询当天有点名抽查日期的任务
     */
    List<SpotCheckTaskDO> findCalltheRollByDate(LocalDate date);

    /**
     * 查询详情
     */
    SpotCheckTaskDTO findById(Integer taskId);

    /**
     * 分页查询
     */
    List<SpotCheckTaskDTO> list(SpotCheckTaskListReqDTO param);

    /**
     * 更新任务状态
     */
    void updateState();

    /**
     * 抽查任务统计
     */
    SpotCheckNumDTO countTasksOfClasses(@Param("classOrgIndexes") List<String> classOrgIndexes);

    /**
     * 查询班级关联的抽查任务
     */
    List<SpotCheckAttendListDTO> findTasksOfClasses(@Param("today") LocalDate today,
                                                    @Param("classOrgIndexes") List<String> classOrgIndexes,
                                                    @Param("state") Integer state,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("searchKey") String searchKey);

    /**
     * 根据任务ID、考勤日期、班级查询考勤学生
     */
    List<SpotCheckAttendDetailDTO> findHaveTaskStudents(@Param("taskId") Integer taskId,
                                                        @Param("date") LocalDate date,
                                                        @Param("classOrgIndexes") List<String> classOrgIndexes,
                                                        @Param("status") Integer status,
                                                        @Param("result") Integer result,
                                                        @Param("searchKey") String searchKey);
}
