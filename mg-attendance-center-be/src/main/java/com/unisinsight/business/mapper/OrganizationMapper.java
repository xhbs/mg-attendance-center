package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.ChannelOfSchoolBO;
import com.unisinsight.business.bo.UserClassMappingBO;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface OrganizationMapper extends Mapper<OrganizationDO> {

    /**
     * 使用 dblink 从uuv同步组织树
     */
    void sync(@Param("host") String host,
              @Param("port") String port,
              @Param("username") String username,
              @Param("password") String password);

    /**
     * 查询所有的通道
     */
    List<ChannelOfSchoolBO> findAllChannels(@Param("host") String host,
                                            @Param("port") String port,
                                            @Param("username") String username,
                                            @Param("password") String password);

    /**
     * 获取班级绑定的班主任信息
     */
    List<UserClassMappingBO> getMappingUser(@Param("host") String host,
                                            @Param("port") String port,
                                            @Param("username") String username,
                                            @Param("password") String password,
                                            @Param("orgIndexes") String orgIndexes);

    /**
     * 批量保存
     */
    void batchSave(@Param("organizations") List<OrganizationDO> organizations);

    /**
     * 批量保存
     */
    void batchUpdate(@Param("organizations") List<OrganizationDO> organizations);

    /**
     * 根据org_index查找
     */
    OrganizationDO selectByOrgIndex(String orgIndex);

    /**
     * 获取组织索引
     */
    String selectIndexPath(@Param("orgIndex") String orgIndex);

    /**
     * 递归查询某个级别的上级节点
     */
    OrganizationDO selectSuperior(@Param("orgIndex") String orgIndex, @Param("subType") Short subType);

    /**
     * 递归查询某个级别的下级节点
     */
    List<String> selectLowerOrgIndex(@Param("orgIndexes") List<String> orgIndexes, @Param("subType") Short subType);

    /**
     * 递归查询某个级别的下级节点
     */
    List<OrganizationDO> selectLowerOrgs(@Param("orgIndex") String orgIndex, @Param("subType") Short subType);


    /**
     * 递归查询某个级别的下级节点,分页
     */
    List<OrganizationDO> selectLowerOrgsLimit(@Param("orgIndex") String orgIndex,
                                              @Param("subType") Short subType,
                                              @Param(value = "limit") Integer limit,
                                              @Param(value = "offset") Integer offset);

    /**
     * 批量查询通过类型
     */
    List<OrganizationDO> selectOrganizationsBatch(@Param(value = "subType") Short subType, @Param(value = "limit") Integer limit, @Param(value = "offset") Integer offset);

    /**
     * 查询子节点
     */
    List<OrganizationDO> getChildren(@Param("orgIndex") String orgIndex, @Param("search") String search);

    List<OrganizationDO> getChildrenByParent(@Param("parent") String orgIndex);

    List<OrganizationDO> getChildrenByParents(@Param("parents") List<String> parents);

    /**
     * 获取所属学校的上级组织
     */
    OrganizationDO getSuperiorOfSchool(@Param("orgIndex") String orgIndex);

    /**
     * 所有学校数量
     *
     * @return
     */
    Integer allSchoolCount(Integer subType);

    /**
     * 省直属学校数量
     *
     * @return
     */
    Integer provinceSchoolCount(String provinceId);

    List<OrganizationDO> selectBySubType(Integer subType);

    List<OrganizationDO> selectOrgIndexAndName();

    void stateBatchUpdate(@Param(value = "list") List<String> orgIndexes,@Param(value = "state") String state);
}
