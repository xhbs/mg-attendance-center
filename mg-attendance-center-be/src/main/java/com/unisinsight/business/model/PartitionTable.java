package com.unisinsight.business.model;

/**
 * 需要分区的表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/21
 */
public interface PartitionTable {
    /**
     * 计算数据具体存到哪一张子表里面
     */
    String getTableSuffix();
}
