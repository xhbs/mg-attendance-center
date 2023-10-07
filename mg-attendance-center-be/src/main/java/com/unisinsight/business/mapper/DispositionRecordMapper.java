package com.unisinsight.business.mapper;

import com.unisinsight.business.model.DispositionRecord;
import com.unisinsight.framework.common.base.Mapper;

import java.util.List;

/**
 * 考勤布控记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
public interface DispositionRecordMapper extends Mapper<DispositionRecord> {

    /**
     * 插入或者更新考勤布控记录
     *
     * @param records 布控记录列表
     */
    void insertOrUpdate(List<DispositionRecord> records);
}
