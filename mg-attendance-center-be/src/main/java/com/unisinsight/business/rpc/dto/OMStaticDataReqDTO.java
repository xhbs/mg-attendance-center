package com.unisinsight.business.rpc.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用于对象管理---静态枚举新增
 */
@Data
public class OMStaticDataReqDTO {

    /**
     * 数据命名
     */
    private String name;

    /**
     * 当前版本仅支持对人员分组的自定义添加，固定传groupType
     */
    private String type;

    /**
     * 当前版本仅支持对人员分组的自定义添加，固定传omperson
     */
    private String formCode;

    /**
     * 数据为分组类型时，此字段作用需要下发至视图库的名单库类型，静态库-0，黑名单-1，白名单库-3，不下发不传
     */
    private String ext1;

    /**
     * 数据为分组类型时，此字段用于下发视图库，静态库-2，黑白名单库-1.不下发不传
     */
    private String ext2;
}
