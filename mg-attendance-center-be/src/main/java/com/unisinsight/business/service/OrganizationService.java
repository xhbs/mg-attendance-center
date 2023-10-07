package com.unisinsight.business.service;

import com.unisinsight.business.bo.UserClassMappingBO;
import com.unisinsight.business.dto.OrgTreeDTO;
import com.unisinsight.business.model.UserInfoDO;

import java.util.List;

/**
 * 人员组织服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/15
 */
public interface OrganizationService {

    /**
     * 从BSS同步组织数据
     */
    void sync();

    /**
     * 获取班级绑定的班主任信息
     */
    List<UserClassMappingBO> getMappingUser(List<String> orgIndexes);

    UserInfoDO getUserInfoByMobile(String userCode);

    OrgTreeDTO allTree();
    OrgTreeDTO tree();
}
