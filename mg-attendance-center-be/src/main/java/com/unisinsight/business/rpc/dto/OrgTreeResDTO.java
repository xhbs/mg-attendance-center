package com.unisinsight.business.rpc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 公共服uuv获取组织架构（根据org_index/org_id）接口返回体
 */
@Data
public class OrgTreeResDTO {

    @ApiModelProperty("组织名")
    @JsonProperty("org_name")
    private String orgName;

    @ApiModelProperty("组织类型")
    private String type;

    @ApiModelProperty("组织id")
    @JsonProperty("org_id")
    private Long orgId;

    @ApiModelProperty("父组织id")
    @JsonProperty("parent_id")
    private Long parentId;

    @ApiModelProperty("组织编码")
    @JsonProperty("org_index")
    private String orgIndex;

    @ApiModelProperty("父组织编码")
    @JsonProperty("org_parent_index")
    private String orgParentIndex;

    @ApiModelProperty("组织编码路径")
    @JsonProperty("index_path")
    private String indexPath;

    @ApiModelProperty("级联id")
    @JsonProperty("cascaded_id")
    private String cascadedId;

    @ApiModelProperty("区域编码")
    @JsonProperty("region_code")
    private String regionCode;

    @ApiModelProperty("区域描述")
    @JsonProperty("region_code_desc")
    private String regionCodeDesc;

    @ApiModelProperty("街道")
    private String street;

    @ApiModelProperty("物主")
    private String owner;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建人")
    @JsonProperty("create_user")
    private String createUser;

    @ApiModelProperty("是否为行政区域")
    @JsonProperty("is_region")
    private String isRegion;

    @ApiModelProperty("排序")
    @JsonProperty("display_order")
    private int displayOrder;

    @ApiModelProperty("组织编码")
    @JsonProperty("org_code")
    private String orgCode;

    @ApiModelProperty("区域id")
    @JsonProperty("region_id")
    private Long regionId;

    @ApiModelProperty("子组织节点")
    private List<OrgTreeResDTO> child;
}
