package com.unisinsight.business.mapper;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 定义需要根据时间分区的表需要实现的一些方法
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/25
 * @since 1.0
 */
public interface PartitionByTimeTableMapper {

    /**
     * 获取最早的分区时间
     */
    LocalDateTime getEarliestPartitionTime();

    /**
     * 创建分区子表
     *
     * @param suffix    子表名称后缀
     * @param startDate 分区键开始日期（包括）
     * @param endDate   分区键结束日期（不包括）
     */
    void createPartitionTable(@Param("suffix") String suffix,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    /**
     * 从旧表迁移数据，然后删除旧表
     */
    void migrateFromOldTable();
}
