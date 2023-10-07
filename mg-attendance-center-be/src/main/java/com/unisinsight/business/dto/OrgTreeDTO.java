package com.unisinsight.business.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName : mg-attendance-center
 * @Description : 组织树
 * @Author : xiehb
 * @Date: 2022/11/10 09:51
 * @Version 1.0.0
 */
@Data
public class OrgTreeDTO {
    private String orgIndex;
    private String orgName;
    private Integer subType;
    private List<OrgTreeDTO> childrens;

}
