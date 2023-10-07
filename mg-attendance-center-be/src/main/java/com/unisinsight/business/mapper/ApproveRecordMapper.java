package com.unisinsight.business.mapper;

import com.unisinsight.business.model.ApproveRecordDO;
import com.unisinsight.framework.common.base.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.util.List;

/**
 * 流程审批记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/26
 */
public interface ApproveRecordMapper extends Mapper<ApproveRecordDO>, InsertUseGeneratedKeysMapper<ApproveRecordDO> {
    /**
     * 批量保存
     */
    void batchSave(List<ApproveRecordDO> list);
}
