package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.response.SubsidBaseResDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticLevelResDTO;
import com.unisinsight.business.service.SubsidCommonService;
import com.unisinsight.business.service.SubsidMatchStaticLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tanggang
 * @version 1.0
 * @description 资助比对controller
 * @email tang.gang@inisinsight.com
 * @date 2021/8/31 14:30
 **/
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/subsid/level")
@Api(tags = "资助比对-节点统计")
public class SubsidMatchStaticLevelController {
    @Autowired
    private SubsidMatchStaticLevelService subsidMatchStaticLevelService;
    @Autowired
    private SubsidCommonService subsidCommonService;


    @PostMapping("/generateSubsidRecords")
    @ApiOperation("生成资助比对记录")
    public Results<SubsidBaseResDTO> generateSubsidRecords(@RequestBody @Validated SubsidBaseReqDTO req) {
        SubsidBaseResDTO subsidBaseResDTO = subsidCommonService.generateSubsidResults(req);
        return Results.success(subsidBaseResDTO);
    }
    @PostMapping("/autoSubsidResults")
    @ApiOperation("生成资助比对记录")
    public Results<SubsidBaseResDTO> autoSubsidResults(@RequestBody @Validated SubsidBaseReqDTO req) {
        SubsidBaseResDTO subsidBaseResDTO = subsidCommonService.autoSubsidResults(req);
        return Results.success(subsidBaseResDTO);
    }

    @PostMapping("/page")
    @ApiOperation("分页查询")
    public Results<PaginationRes<SubsidMatchStaticLevelResDTO>> getLevelPage(@RequestBody @Validated SubsidMatchStaticLevelReqDTO req) {
        PaginationRes<SubsidMatchStaticLevelResDTO> page = subsidMatchStaticLevelService.getPageByHandle(req);
        return Results.success(page);
    }


    @GetMapping("/preCheckExportExcel")
    @ApiOperation("导出EXCEL预校验")
    public Results preCheckExportExcel( @RequestParam("subsid_rule_id")Integer subsidRuleId, @RequestParam("org_parent_index")String orgIndex ) {
        Results results = subsidCommonService.preCheckExportExcel(subsidRuleId, orgIndex);
        return results;
    }

    @GetMapping("/export")
    @ApiOperation("导出资助比对通过名单EXCEL（这个暂时保留，现场可能后续有变更）")
    public void exportExcel( @RequestParam("subsid_rule_id")Integer subsidRuleId, @RequestParam("org_parent_index")String orgIndex , HttpServletResponse resp) {
         subsidCommonService.exportExcel(subsidRuleId, orgIndex, resp);
    }

    @GetMapping("/exportPageExcel")
    @ApiOperation("导出页面excel")
    public void exportPageExcel( @RequestParam("subsid_rule_id")Integer subsidRuleId, @RequestParam("org_parent_index")String orgParentIndex , HttpServletResponse resp) throws IOException {
        subsidMatchStaticLevelService.exportExcel(subsidRuleId,orgParentIndex,resp);
    }



}
