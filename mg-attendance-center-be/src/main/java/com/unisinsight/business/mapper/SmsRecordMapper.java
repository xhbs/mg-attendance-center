package com.unisinsight.business.mapper;

import com.unisinsight.business.model.SmsRecordDO;
import com.unisinsight.framework.common.base.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

/**
 * 短信记录表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/10
 */
public interface SmsRecordMapper extends Mapper<SmsRecordDO>, InsertUseGeneratedKeysMapper<SmsRecordDO> {
}