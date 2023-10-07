/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.controller;

import com.unisinsight.business.bo.SubsidStuCountBO;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.ExcelCheckResult;
import com.unisinsight.business.dto.SubsidStuListBaseExcelDTO;
import com.unisinsight.business.dto.SubsidStuListStipendExcelDTO;
import com.unisinsight.business.dto.SubsidStuListWaiveExcelDTO;
import com.unisinsight.business.dto.request.SubsidStuListSyncReqDTO;
import com.unisinsight.business.dto.response.SubsidListUploadResDTO;
import com.unisinsight.business.service.SubsidStuListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

import static com.unisinsight.framework.common.exception.BaseErrorCode.PARAMETER_EMPTY;

/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:06:31
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/subsid/stu-list")
@Api(tags = "资助比对-名单信息同步")
public class SubsidStuListController {
    @Resource
    private SubsidStuListService subsidStuListService;

    @PostMapping("/subsidStuListAutoSync")
    @ApiOperation("接口自动同步")
    public Results<Void> subsidStuListSync(@RequestBody SubsidStuListSyncReqDTO subsidStuListSyncReqDTO) {
        subsidStuListService.subsidStuListSync(subsidStuListSyncReqDTO);
        return Results.success();
    }
    @PostMapping("/findSubListIndex")
    @ApiOperation("名单库人数统计")
    public Results<List<SubsidStuCountBO>> subsidStuListSync() {
        List<SubsidStuCountBO> subListIndex = subsidStuListService.findSubListIndex();
        return Results.success(subListIndex);
    }

    @PostMapping("/uploadSubsidStuExcelHandleSync")
    @ApiOperation("手动上传excel同步")
    public Results<SubsidListUploadResDTO> uploadSubsidStuListByHandle(MultipartFile file, @RequestParam(value = "prj_type") Integer prjType ) {

        ExcelCheckResult<SubsidStuListBaseExcelDTO> excelCheckResult ;
        if(prjType == 0){
            excelCheckResult = ExcelUtil.importExcel(file, SubsidStuListWaiveExcelDTO.class, 0, 5, null, 50000);
        }else{
            excelCheckResult = ExcelUtil.importExcel(file, SubsidStuListStipendExcelDTO.class, 0, 5, null, 50000);
        }
        List<SubsidStuListBaseExcelDTO> successList = excelCheckResult.getSuccessList().subList(5,excelCheckResult.getSuccessList().size());
        if(CollectionUtils.isEmpty(successList)){
            SubsidListUploadResDTO resDTO = SubsidListUploadResDTO.builder().respCode(0).message("无名单数据").build();
            return  Results.success(resDTO) ;
        }
        SubsidListUploadResDTO resDTO = subsidStuListService.uploadSubsidStuListByHandle(successList, prjType);
        return Results.success(resDTO);
    }

}
