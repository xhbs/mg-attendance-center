package com.unisinsight.business.mapper;

import com.unisinsight.business.model.TaskAttendanceDateDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 抽查任务考勤日期表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/2
 */
public interface TaskAttendanceDateMapper extends Mapper<TaskAttendanceDateDO> {

    /**
     * 查找任务的考勤日期
     */
    List<String> findByTask(@Param("taskId") Integer taskId);
}