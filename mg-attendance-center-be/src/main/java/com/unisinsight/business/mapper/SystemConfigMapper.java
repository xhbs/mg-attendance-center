package com.unisinsight.business.mapper;

import com.unisinsight.business.model.SystemConfigDO;
import com.unisinsight.framework.common.base.Mapper;

import java.util.List;

/**
 * 系统配置
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/10/19
 * @since 1.0
 */
public interface SystemConfigMapper extends Mapper<SystemConfigDO> {
    /**
     * 初始化系统配置
     *
     * @param configs 默认配置列表
     */
    void saveConfigs(List<SystemConfigDO> configs);
}