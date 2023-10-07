package com.unisinsight.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/25
 * @since 1.0
 */
@Mapper
public interface CommonTableMapper {

    /**
     * 表是否存在
     */
    boolean tableExists(@Param("tableName") String tableName);

    /**
     * 获取分区表的所有子表
     */
    Set<String> getChildTables(@Param("tableName") String tableName);

    /**
     * 获取已创建最新的子表分区键的最大日期
     */
    LocalDate getPartitionedLatestDate(@Param("tableName") String tableName);

    /**
     * 获取已创建最新的子表分区键的最小日期
     */
    LocalDate getPartitionedEarliestDate(@Param("tableName") String tableName);

    /**
     * 创建插件
     */
    void createExtensions(@Param("extensions") String[] extensions);
}
