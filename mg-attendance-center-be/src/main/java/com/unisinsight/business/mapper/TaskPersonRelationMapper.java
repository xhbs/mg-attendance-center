package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.PersonListOfClassBO;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.bo.TaskPersonBO;
import com.unisinsight.business.dto.request.StuListReq;
import com.unisinsight.business.model.TaskPersonRelationDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 抽查考勤任务与人员关联表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
public interface TaskPersonRelationMapper extends Mapper<TaskPersonRelationDO> {

    /**
     * 查找抽查任务的目标考勤人员（只关联在校人员）
     */
    void saveTaskPersons(@Param("taskId") Integer taskId,
                         @Param("orgIndexes") List<String> orgIndexes,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("absenceRate") Integer absenceRate);

    /**
     * 查找抽查任务的目标考勤人员（只关联在校人员）
     */
    void saveTaskPersonsCtr(@Param("taskId") Integer taskId,
                            @Param("orgIndexes") List<String> orgIndexes,@Param("callTheRollFirstTaskId") Integer callTheRollFirstTaskId);

    /**
     * 删除已经不在校的学生
     */
    void deleteNotAtSchoolPersons();

    /**
     * 查找当天有考勤任务的人员
     */
    List<TaskPersonBO> findHaveTaskPersons(@Param("date") LocalDate date,
                                           @Param("personNos") Set<String> personNos);

    /**
     * 查找当天有考勤任务但是缺勤的人员
     */
    List<TaskPersonBO> findAbsencePersonsAtDate(@Param("date") LocalDate date);

    /**
     * 查找当天有考勤任务的人员
     */
    List<PersonOfClassBO> findPersonsAtDate(@Param("date") LocalDate date);

    /**
     * 查询学校下关联的抽查学生集合
     */
    List<String> findPersonsOfSchool(@Param("orgIndex") String orgIndex);

    /**
     * 查找某天某学生的考勤任务id
     */
    List<Integer> findPersonTask(@Param("date") LocalDate date,
                                 @Param("personNo") String personNo);

    List<PersonListOfClassBO> findPersonListByTaskId(@Param("req") StuListReq req, List<String> orgIndex);

}
