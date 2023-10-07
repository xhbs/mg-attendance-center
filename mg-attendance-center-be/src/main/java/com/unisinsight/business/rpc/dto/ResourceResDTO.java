package com.unisinsight.business.rpc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : LiuYang  [1005859278@qq.com]
 * @date : 2020/7/7
 * @desc
 */
@Data
public class ResourceResDTO {
    /**
     * 节点名称
     */
    @JsonProperty("org_name")
    @ApiModelProperty("节点名称")
    private String orgName;

    /**
     * 节点id
     */
    @JsonProperty("org_id")
    @ApiModelProperty("节点id")
    private Integer orgId;

    /**
     * 父节点id
     */
    @JsonProperty("parent_id")
    @ApiModelProperty("父节点id")
    private Long parentId;

    /**
     * 节点index
     */
    @JsonProperty("org_index")
    @ApiModelProperty("节点index")
    private String orgIndex;

    /**
     * 父节点index
     */
    @JsonProperty("org_parent_index")
    @ApiModelProperty("父节点index")
    private String orgParentIndex;

    /**
     * index_path
     */
    @JsonProperty("index_path")
    @ApiModelProperty("indexPath")
    private String indexPath;

    /**
     * 组织类型
     */
    @JsonProperty("sub_type")
    @ApiModelProperty("组织类型")
    private Short subType;

    /**
     * 小区编号
     */
    @JsonProperty("description")
    @ApiModelProperty("小区编号")
    private String description;

    /**
     * 子节点
     */
    @ApiModelProperty("子节点")
    private List<ResourceResDTO> child;
}
