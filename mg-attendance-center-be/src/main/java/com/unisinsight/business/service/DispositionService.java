package com.unisinsight.business.service;

import java.util.List;

/**
 * 布控服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
public interface DispositionService {

    /**
     * 根据名单库布控
     *
     * @param tabIds 名单库ID列表
     */
    void createDispositions(List<String> tabIds);

    /**
     * 根据名单库删除布控
     *
     * @param tabIds 名单库ID列表
     */
    void deleteDispositions(List<String> tabIds);
}
