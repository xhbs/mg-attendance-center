package com.unisinsight.business.mapper;

import com.unisinsight.business.dto.request.AppealPersonListReqDTO;
import com.unisinsight.business.dto.response.AppealPersonListResDTO;
import com.unisinsight.business.dto.response.AppealStatListResDTO;
import com.unisinsight.business.model.AppealPersonDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 申诉记录关联人员
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
public interface AppealPersonMapper extends Mapper<AppealPersonDO> {

    /**
     * 查找申诉记录关联的人员列表
     *
     * @param recordId 申诉记录
     * @return 人员编号列表
     */
    List<String> findPersonNosOfRecordId(@Param("recordId") Integer recordId);

    /**
     * 申诉统计 - 分页查询，按照组织统计
     */
    List<AppealStatListResDTO> statByOrg(@Param("orgIndexPaths") List<String> orgIndexPaths,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /**
     * 申诉统计 - 分页查询，按照班级统计
     */
    List<AppealStatListResDTO> statByClass(@Param("orgIndexes") List<String> orgIndexes,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * 申诉统计 - 分页查询，按照学生统计
     */
    List<AppealPersonListResDTO> statByPerson(AppealPersonListReqDTO req);
}