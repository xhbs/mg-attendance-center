package com.unisinsight.business.service;

import java.time.LocalDate;
import java.util.List;

/**
 * 抽查任务与考勤人员关联服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/2
 */
public interface TaskPersonRelationService {

    /**
     * 保存抽查任务与考勤人员关联关系，异步操作
     */
    void saveTaskPersons(Integer taskId, List<String> classOrgIndexes, LocalDate startDate, LocalDate endDate,
                         Integer targetAbsenceWeeks);

    /**
     * 保存抽查任务与考勤人员关联关系
     * @param taskId
     * @param classOrgIndexes
     */
    void saveTaskPersonsCtr(Integer taskId, List<String> classOrgIndexes,Integer callTheRollFirstTaskId);

    /**
     * 删除抽查任务与考勤人员关联关系
     */
    void deleteByTaskIds(List<Integer> taskIds);

    /**
     * 根据人员编号删除
     */
    void deleteByPersonNos(List<String> personNos);
}
