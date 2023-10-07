package com.unisinsight.business.dto.response;

import com.unisinsight.business.rpc.dto.OrgTreeResDTO;
import lombok.Data;

import java.util.List;

/**
 * 查询用户绑定的组织列表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/31
 * @since 1.0
 */
@Data
public class UserOrgListResDTO {

    /**
     * 绑定组织列表
     */
    private List<OrgTreeResDTO> classOrgList;
}
