package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.SubsidBaseReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticStuExcelReqDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticStuReqDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticStuResDTO;
import com.unisinsight.business.service.SubsidCommonService;
import com.unisinsight.business.service.SubsidMatchStaticStuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanggang
 * @version 1.0
 * @description 资助比对controller
 * @email tang.gang@inisinsight.com
 * @date 2021/8/31 14:30
 **/
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/subsid/stu")
@Api(tags = "资助比对-学生统计")
public class SubsidMatchStaticStuController {
    @Autowired
    private SubsidMatchStaticStuService subsidMatchStaticStuService;
    @Autowired
    private SubsidCommonService subsidCommonService;

    @PostMapping("/static/page")
    @ApiOperation("分页查询")
    public Results<PaginationRes<SubsidMatchStaticStuResDTO>> getStuPage(@RequestBody @Validated SubsidMatchStaticStuReqDTO req) {
        PaginationRes<SubsidMatchStaticStuResDTO> page = subsidMatchStaticStuService.getPage(req);
        return Results.success(page);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询详情")
    public Results<SubsidMatchStaticStuResDTO> get(@PathVariable("id") Long id) {
        return Results.success(subsidMatchStaticStuService.findById(id));
    }

    @GetMapping("/preCheckExportExcel")
    @ApiOperation("导出EXCEL预校验")
    public Results preCheckExportExcel( @RequestParam("subsid_rule_id")Integer subsidRuleId, @RequestParam("org_parent_index")String orgIndex ) {
        Results results = subsidCommonService.preCheckExportExcel(subsidRuleId, orgIndex);
        return results;
    }

    @GetMapping("/export")
    @ApiOperation("导出EXCEL（这个暂时保留，现场可能后续有变更）")
    public void exportStuExcel( @RequestParam("subsid_rule_id")Integer subsidRuleId, @RequestParam("org_parent_index")String orgIndex , HttpServletResponse resp) {
        subsidCommonService.exportExcel(subsidRuleId,orgIndex,resp);
    }

    @GetMapping("/exportPageExcel")
    @ApiOperation("导出页面excel")
    public void exportPageExcel(SubsidMatchStaticStuExcelReqDTO reqDTO, HttpServletResponse resp) {
        subsidMatchStaticStuService.exportExcel(reqDTO,resp);
    }


}
