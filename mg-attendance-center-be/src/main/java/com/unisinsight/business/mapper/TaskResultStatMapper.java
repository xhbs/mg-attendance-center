package com.unisinsight.business.mapper;

import com.unisinsight.business.dto.request.TaskResultStatReqDTO;
import com.unisinsight.business.dto.response.TaskResultStatResDTO;
import com.unisinsight.business.model.TaskResultStatDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 抽查考勤统计表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/6
 */
public interface TaskResultStatMapper extends Mapper<TaskResultStatDO> {

    /**
     * 批量保存或更新
     */
    void batchSave(@Param("records") List<TaskResultStatDO> records);

    /**
     * 分页查询
     */
    List<TaskResultStatResDTO> list(TaskResultStatReqDTO param);
}