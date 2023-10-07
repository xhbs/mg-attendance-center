package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.SubsidMatchStuDetailDTO;
import com.unisinsight.business.dto.request.SubsidMatchStaticLevelReqDTO;
import com.unisinsight.business.dto.request.SubsidStuAttendanceResultsReqDTO;
import com.unisinsight.business.dto.response.SubsidMatchStaticStuResDTO;
import com.unisinsight.business.dto.response.SubsidStuAttendanceResultsResDTO;
import com.unisinsight.business.service.SubsidStuAttendanceResultsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author tanggang
 * @version 1.0
 * @description 资助比对controller
 * @email tang.gang@inisinsight.com
 * @date 2021/8/31 14:30
 **/
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/subsid/stu/deatail")
@Api(tags = "资助比对-学生考勤详情")
public class SubsidMatchStaticStuDetailController {

    @Autowired
    private SubsidStuAttendanceResultsService subsidStuAttendanceResultsService;

    @PostMapping("/page")
    @ApiOperation("分页查询")
    public Results<PaginationRes<SubsidStuAttendanceResultsResDTO>> getStuDetailPage(@RequestBody @Validated SubsidStuAttendanceResultsReqDTO req) {
        PaginationRes<SubsidStuAttendanceResultsResDTO> page = subsidStuAttendanceResultsService.getPage(req);
        return Results.success(page);
    }

    @GetMapping("/export")
    @ApiOperation("导出EXCEL")
    public void exportStuDetailExcel( @Validated SubsidStuAttendanceResultsReqDTO req, HttpServletResponse resp) {
        subsidStuAttendanceResultsService.exportExcel(req,resp);
    }


}
