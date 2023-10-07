package com.unisinsight.business.common.utils.excel;

import lombok.Getter;
import lombok.Setter;

/**
 * excel列信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/22
 * @since 1.0
 */
@Getter
@Setter
public class Column<T> {

    /**
     * Excel字段名称
     */
    private String name;

    /**
     * Excel行宽
     */
    private int width;

    /**
     * 数据库的值到excel导出值的转换
     */
    private FieldMapper<T> mapper;

    public Column(String name, int width, FieldMapper<T> mapper) {
        this.name = name;
        this.width = width;
        this.mapper = mapper;
    }
}