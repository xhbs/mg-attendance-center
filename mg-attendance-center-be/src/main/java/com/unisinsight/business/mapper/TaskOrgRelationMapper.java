package com.unisinsight.business.mapper;

import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.model.TaskOrgRelationDO;
import com.unisinsight.framework.common.base.Mapper;

import java.util.List;

/**
 * 抽查任务关联组织表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/2
 */
public interface TaskOrgRelationMapper extends Mapper<TaskOrgRelationDO> {

    /**
     * 查找任务关联的所有学校
     */
    List<OrganizationDO> findSchoolsOfTask(Integer taskId);
}