package com.unisinsight.business.rpc.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 对象管理分组分页返回对象
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/4/13
 * @since 1.0
 */
@Data
public class OMGroupDTO {
    private String createBy;
    private Date createTime;
    private String description;
    private String groupCode;
    private String groupName;
    private Long id;
    private String objectType;
    private String parent;
    private String sendLib;
    private Integer status;
    private String tabId;
    private String tabType;
    private Integer type;
    private Date updateTime;

    private List<OMGroupDTO> children;
}
