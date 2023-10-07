/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service;

import com.unisinsight.business.bo.*;
import com.unisinsight.business.dto.SubsidStuListBaseExcelDTO;
import com.unisinsight.business.dto.request.SubsidStuListSyncReqDTO;
import com.unisinsight.business.dto.response.SubsidListUploadResDTO;
import com.unisinsight.business.model.SubsidStuListDO;

import java.util.List;


/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:06:31
 * @since 1.0
 */
public interface SubsidStuListService  {
    /**
     *  手工上传excel
     * @param uploadList,excel记录
     * @param prjType ，0-助学金，1-免费
     * @return
     */
    SubsidListUploadResDTO uploadSubsidStuListByHandle(List<SubsidStuListBaseExcelDTO> uploadList , Integer prjType) ;

    void subsidStuListSync(SubsidStuListSyncReqDTO subsidStuListSyncReqDTO);

     PaginationRes<SubsidStuBO> getPage(String subListIndex, PaginationReq paginationReq) ;

     PaginationRes<SubsidStuListDO> getSubsidStuExcelPage(QuerySubsidStuBO querySubsidStuBO, PaginationReq paginationReq) ;
     /**
       *@description  分页查询学生编号
       *@param
       *@return
       *@date    2021/9/6 10:34
       */
     PaginationRes<String> getPersonNoListPage(String subListIndex, PaginationReq paginationReq) ;


    List<SubsidStuCountBO> findSubListIndex();
}
