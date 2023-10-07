package com.unisinsight.business.service;

import com.unisinsight.business.model.OriginalRecordDO;

import java.util.List;


/**
 * @author chengnian [cheng.nian@unisinsight.com]
 * @date 2020/09/03 12:36:09
 * @since 1.0
 */
public interface OriginalRecordService {

    /**
     * 批量保存原始记录
     */
    void batchSave(List<OriginalRecordDO> list);
}
