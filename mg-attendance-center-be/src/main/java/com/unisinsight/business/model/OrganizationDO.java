package com.unisinsight.business.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organizations")
public class OrganizationDO {

    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 组织索引全名称
     */
    @Column(name = "org_index")
    private String orgIndex;

    /**
     * 组织索引全名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 组织索引全名称
     */
    @Column(name = "index_path")
    private String indexPath;

    /**
     * 组织索引全名称
     */
    @Column(name = "index_path_name")
    private String indexPathName;

    /**
     * 组织索引全名称
     */
    @Column(name = "org_parent_index")
    private String orgParentIndex;

    /**
     * 组织类型 {@link com.unisinsight.business.common.enums.OrgType}
     */
    @Column(name = "sub_type")
    private Short subType;

    /**
     * 显示顺序
     */
    @Column(name = "display_order")
    private Float displayOrder;

    /**
     * 是否是虚拟组织
     */
    @Column(name = "is_virtual")
    private Boolean isVirtual;
}
