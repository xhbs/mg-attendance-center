package com.unisinsight.business.common.utils.excel;

/**
 * excel字段转换
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/22
 * @since 1.0
 */
public interface FieldMapper<T> {

    Object convert(int index, T value);
}