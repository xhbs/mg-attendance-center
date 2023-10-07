package com.unisinsight.business.mapper;

import com.unisinsight.business.model.FeelTaskRelationDO;
import com.unisinsight.framework.common.base.Mapper;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

/**
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/9/11
 */
@Repository
public interface FeelTaskRelationMapper extends Mapper<FeelTaskRelationDO>, InsertUseGeneratedKeysMapper<FeelTaskRelationDO> {
    int insert(FeelTaskRelationDO feelTaskRelationDO);
}
