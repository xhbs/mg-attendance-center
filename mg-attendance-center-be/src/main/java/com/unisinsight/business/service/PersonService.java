package com.unisinsight.business.service;

import com.unisinsight.business.bo.PersonBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.PersonReqDTO;
import com.unisinsight.business.rpc.dto.OMPersonDTO;

import java.util.List;

/**
 * 考勤人员管理服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/4/12
 * @since 1.0
 */
public interface PersonService {

    /**
     * 同步添加
     */
    void addSync(List<OMPersonDTO> persons);

    /**
     * 同步更新
     */
    void updateSync(List<OMPersonDTO> persons);

    /**
     * 删除人员
     */
    void deleteByPersonNos(List<String> personNos);

    /**
     * 分页查询
     */
    PaginationRes<PersonBO> query(PersonReqDTO req);

    /**
     * 查询总条数
     */
    Integer selectCountByCondition(PersonBO personBO);
}
