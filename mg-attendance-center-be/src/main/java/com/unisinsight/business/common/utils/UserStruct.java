package com.unisinsight.business.common.utils;

import cn.hutool.core.collection.CollUtil;
import com.unisinsight.business.common.enums.AdminRole;
import com.unisinsight.business.common.enums.OrgType;
import com.unisinsight.business.rpc.dto.UserDetailResDTO;
import com.unisinsight.business.dto.response.UserOrgListResDTO;
import com.unisinsight.business.rpc.InfraClient;
import com.unisinsight.business.rpc.dto.OrgTreeResDTO;
import com.unisinsight.business.rpc.dto.RoleInfo;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对用户信息、角色、组织封装的结构体
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/23
 * @since 1.0
 */
@Slf4j
public class UserStruct {
    /**
     * 当前用户编号和姓名
     */
    private User user;

    /**
     * 用户详情
     */
    private UserDetailResDTO userDetail;

    /**
     * 组织编号
     */
    private String indexPath;

    /**
     * 用户的角色列表
     */
    private List<RoleInfo> roles;

    /**
     * 用户的角色列表
     */
    private HashSet<String> roleSet;

    /**
     * 拥有最高的角色信息
     */
    private RoleInfo highestRole;

    /**
     * 拥有的最高等级的管理员
     */
    private AdminRole adminRole;

    /**
     * 所属学校
     */
    private OrganizationDO school;

    /**
     * 绑定的班级列表
     */
    private List<String> classes;

    /**
     * 所属学校的上级组织 可能是县、市、省
     */
    private OrganizationDO superiorOfSchool;

    private OrganizationMapper organizationMapper;

    private InfraClient infraClient;

    public UserStruct(User user, OrganizationMapper organizationMapper, InfraClient infraClient) {
        this.user = user;
        this.organizationMapper = organizationMapper;
        this.infraClient = infraClient;
    }

    /**
     * 获取用户编号
     */
    public String getUserCode() {
        return user.getUserCode();
    }

    /**
     * 获取用户姓名
     */
    public String getUserName() {
        return user.getUserName();
    }

    /**
     * 获取用户详情
     */
    public UserDetailResDTO getUserDetail() {
        if (userDetail == null) {
            userDetail = infraClient.getUserDetail(user.getUserCode());
        }
        return userDetail;
    }

    /**
     * 获取组织编码
     */
    public String getOrgIndex() {
        if (userDetail == null) {
            userDetail = infraClient.getUserDetail(user.getUserCode());
        }
        return userDetail.getOrgIndex();
    }

    /**
     * 获取组织名称
     */
    public String getOrgName() {
        if (userDetail == null) {
            userDetail = infraClient.getUserDetail(user.getUserCode());
        }
        return userDetail.getOrgName();
    }

    /**
     * 获取组织索引
     */
    public String getIndexPath() {
        if (indexPath == null) {
            UserDetailResDTO userDetail = getUserDetail();
            indexPath = organizationMapper.selectIndexPath(userDetail.getOrgIndex());
        }
        return indexPath;
    }

    /**
     * 获取拥有的角色列表
     */
    public List<RoleInfo> getRoles() {
        if (CollUtil.isEmpty(roles)) {
            roles = infraClient.getRoles(user.getUserCode());
        }
        log.info("获取用户角色:{}", roles);
        return roles;
    }

    /**
     * 获取角色编码列表
     */
    public HashSet<String> getRoleSet() {
        if (roleSet == null) {
            List<RoleInfo> roles = getRoles();
            if (CollectionUtils.isNotEmpty(roles)) {
                roleSet = new HashSet<>(2);
                for (RoleInfo role : roles) {
                    for (AdminRole value : AdminRole.values()) {
                        if (value.getCode().equals(role.getRoleCode())) {
                            roleSet.add(value.getCode());
                        }
                    }
                }
            }
        }
        return roleSet;
    }

    /**
     * 获取当前管理员的所有角色，然后对角色等级排序，取最高的角色
     */
    public RoleInfo getHighestRole() {
        if (highestRole == null) {
            List<RoleInfo> roles = getRoles();
            if (CollectionUtils.isNotEmpty(roles)) {
                highestRole = roles.stream()
                        .sorted(Comparator.comparing(roleInfo -> {
                                    if (roleInfo.getRoleCode() == null) {
                                        return null;
                                    }
                                    AdminRole role = AdminRole.valueOfCode(roleInfo.getRoleCode());
                                    if (role == null) {
                                        return null;
                                    }
                                    return role.getLevel();
                                }, Comparator.nullsLast(Short::compareTo))
                        )
                        .collect(Collectors.toList())
                        .get(0);
            }
        }
        return highestRole;
    }

    /**
     * 获取用户的最高等级的管理员角色
     */
    public AdminRole getAdminRoleCode() {
        if (adminRole == null) {
            RoleInfo highestRole = getHighestRole();
            if (highestRole != null) {
                adminRole = AdminRole.valueOfCode(highestRole.getRoleCode());
            }
        }
        return adminRole;
    }

    /**
     * 获取所属学校
     */
    public OrganizationDO getSchool() {
        if (school == null) {
            school = organizationMapper.selectSuperior(getUserDetail().getOrgIndex(), OrgType.SCHOOL.getValue());
        }
        return school;
    }

    /**
     * 获取所属学校的上级组织
     */
    public OrganizationDO getSuperiorOfSchool() {
        if (superiorOfSchool == null) {
            OrganizationDO school = getSchool();
            if (school == null) {
                return null;
            }
            superiorOfSchool = organizationMapper.getSuperiorOfSchool(school.getOrgIndex());
        }
        return superiorOfSchool;
    }

    /**
     * 获取班主任绑定的班级列表
     *
     * @return 班级索引列表
     */
    public List<String> getClassesIndexPaths() {
        if (classes == null) {
            List<OrgTreeResDTO> orgList = getClassesList();
            if (orgList == null || orgList.isEmpty()) {
                return null;
            }
            classes = orgList.stream()
                    .map(org -> org.getIndexPath() + org.getOrgIndex())
                    .collect(Collectors.toList());
        }
        return classes;
    }

    /**
     * 获取班主任绑定的班级列表
     *
     * @return 班级组织编号列表
     */
    public List<String> getClassesIndex() {
        if (classes == null) {
            List<OrgTreeResDTO> orgList = getClassesList();
            if (orgList == null || orgList.isEmpty()) {
                return null;
            }
            classes = orgList.stream()
                    .map(OrgTreeResDTO::getOrgIndex)
                    .collect(Collectors.toList());
        }
        return classes;
    }

    /**
     * 获取班主任绑定的班级列表
     *
     * @return 班级列表
     */
    public List<OrgTreeResDTO> getClassesList() {
        UserOrgListResDTO res = infraClient.getClasses(user.getUserCode());
        return res.getClassOrgList();
    }
}
