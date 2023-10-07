package com.unisinsight.business.rpc.dto;

import lombok.Data;

/**
 * 对象管理--静态枚举类返回体
 */
@Data
public class OMStaticDataResDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 数据编码
     */
    private String code;
    /**
     * 数据名称
     */
    private String name;
    /**
     * 数据分类
     */
    private String type;
    /**
     * 扩展字段1
     */
    private String ext1;

    /**
     * 扩展字段2
     */
    private String ext2;
    /**
     * 备注
     */
    private String description;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 是否为默认数据
     */
    private String isDefault;
    /**
     * 数据子分类
     */
    private String formCode;
}
