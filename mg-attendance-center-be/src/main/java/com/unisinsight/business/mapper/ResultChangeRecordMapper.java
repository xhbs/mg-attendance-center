package com.unisinsight.business.mapper;

import com.unisinsight.business.model.ResultChangeRecordDO;
import com.unisinsight.framework.common.base.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

/**
 * 考勤记录变更表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/17
 */
public interface ResultChangeRecordMapper extends Mapper<ResultChangeRecordDO>,
        InsertUseGeneratedKeysMapper<ResultChangeRecordDO> {
}
