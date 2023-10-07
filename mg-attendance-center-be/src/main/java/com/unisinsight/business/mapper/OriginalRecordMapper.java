package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.OriginalRecordBO;
import com.unisinsight.business.dto.CaptureRecordDTO;
import com.unisinsight.business.model.OriginalRecordDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface OriginalRecordMapper extends Mapper<OriginalRecordDO>, PartitionByTimeTableMapper {

    /**
     * 批量插入
     */
    void batchSave(List<OriginalRecordDO> list);

    /**
     * 获取最近三次抓拍记录
     */
    List<CaptureRecordDTO> findRecentCaptureRecords(String personNo, LocalDateTime time, Integer count);

    /**
     * 获取一定数量未处理的原始数据
     */
    List<OriginalRecordBO> fetchUnProcessRecordOrderly(@Param(value = "idOffset") Long idOffset,
                                                       @Param(value = "timeLimit") LocalDateTime timeLimit,
                                                       @Param(value = "limit") int limit);

    /**
     * 筛选当天有打卡的人员
     */
    Set<String> filterPersonNos(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime,
                                @Param("personNos") List<String> personNos);
}