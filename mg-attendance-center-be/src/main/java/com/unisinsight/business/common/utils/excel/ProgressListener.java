package com.unisinsight.business.common.utils.excel;

/**
 * excel导出进度回调
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/22
 * @since 1.0
 */
public interface ProgressListener {

    void update(float progress);
}