package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.PersonBO;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.bo.PersonOfSchoolBO;
import com.unisinsight.business.dto.PersonCountDTO;
import com.unisinsight.business.dto.PersonDTO;
import com.unisinsight.business.dto.request.PersonReqDTO;
import com.unisinsight.business.dto.response.StudentDTO;
import com.unisinsight.business.model.PersonDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤人员表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
public interface PersonMapper extends Mapper<PersonDO> {
    /**
     * 使用 dblink 从对象管理同步考勤人员
     */
    void sync(@Param("host") String host,
              @Param("port") String port,
              @Param("username") String username,
              @Param("password") String password);

    /**
     * 使用 dblink 从对象管理同步考勤人员
     */
    void syncImage(@Param("host") String host,
              @Param("port") String port,
              @Param("username") String username,
              @Param("password") String password);

    /**
     * 批量保存或更新
     */
    void batchSaveOrUpdate(@Param("persons") List<PersonDO> persons);

    void batchSaveOrUpdateNew(@Param("persons") List<PersonDO> persons);

    /**
     * 根据人员编号查询
     */
    PersonDTO findByPersonNo(@Param("personNo") String personNo);

    /**
     * 查找学校下的学生
     */
    List<PersonOfSchoolBO> findPersonsOfSchool(@Param("personNos") List<String> personNos);

    /**
     * 查找班级的学生
     */
    List<PersonOfClassBO> findPersonsOfClass(@Param("personNos") List<String> personNos);

    /**
     * 根据人员编号查询
     */
    PersonBO findPersonBO(@Param("personNo") String personNo);

    /**
     * 分页查询
     */
    List<PersonBO> query(PersonReqDTO reqDTO);

    /**
     * 根据组织编号查学生(只查询在籍的)
     */
    List<StudentDTO> findByClasses(@Param("classOrgIndexes") List<String> classOrgIndexes,
                                   @Param("searchKey") String searchKey);

    /**
     * 根据orgindex分组统计学生数量
     */
    List<PersonCountDTO> selectCountGroupByOrgIndex(
            @Param("registered") Boolean registered,
            @Param("atSchool") Boolean atSchool,
            @Param("orgIndexs") List<String> orgIndexs);

}
