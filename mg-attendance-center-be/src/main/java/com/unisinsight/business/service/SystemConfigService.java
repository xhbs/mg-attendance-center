
package com.unisinsight.business.service;

import com.unisinsight.business.dto.SystemConfigDTO;
import com.unisinsight.business.model.SystemConfigDO;

import java.util.List;

/**
 * 系统配置服务
 *
 * @author wangyi01
 * @date 2020/7/16
 */
public interface SystemConfigService {

    /**
     * 新增
     */
    void add(SystemConfigDO config);

    /**
     * 更新
     */
    void updateValue(String key, String value);

    /**
     * 获取系统配置项
     */
    String getConfigValue(String key);

    /**
     * 获取系统配置项
     */
    Integer getIntegerConfigValue(String key);

    /**
     * 获取系统配置项
     */
    Long getLongConfigValue(String key);

    /**
     * 查询提供给前端的配置项
     */
    List<SystemConfigDTO> getFrontendConfigs();
}
