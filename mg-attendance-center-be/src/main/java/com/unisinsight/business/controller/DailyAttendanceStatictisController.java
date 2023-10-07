package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.common.utils.TransferDataUtils;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.service.IDailyAttendanceStaticLevelServcie;
import com.unisinsight.business.service.IDailyAttendanceperiodStuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/biz-scene/v1/attendance/statictis")
@Api(tags = "日常考勤 - 组织结果统计")
public class DailyAttendanceStatictisController {

    @Autowired
    private IDailyAttendanceperiodStuService dailyAttendanceperiodStuService;
    @Value("${excel-export.max-size:10000}")
    private int excelMaxExportRows;
    @Autowired
    private TransferDataUtils transferDataUtils;
    @Autowired
    private IDailyAttendanceStaticLevelServcie dailyAttendanceStaticHighLvlServcie;

    @PostMapping("/high/level/page")
    @ApiOperation("高级节点考勤统计分页查询")
    public Results<PaginationRes<DailyAttendanceHighLvlStaticDTO>> highLvlList(@RequestBody @Validated DailyAttendanceHighLvlStaticQueryReqDTO req) {
        PaginationRes<DailyAttendanceHighLvlStaticDTO> page = dailyAttendanceStaticHighLvlServcie.getPageHandle(req);
        return Results.success(page);
    }


    @PostMapping("/stu/page")
    @ApiOperation("学生考勤统计分页查询")
    public Results<PaginationRes<DailyAttendanceStuStaticDTO>> stuList(@RequestBody @Validated DailyAttendanceStuStaticQueryReqDTO req) {
        PaginationRes<DailyAttendanceStuStaticDTO> page = dailyAttendanceperiodStuService.getPage(req);
        return Results.success(page);
    }

    @GetMapping("/level/export")
    @ApiOperation("高级节点考勤统计导出")
    public void exportLvlExcel( @Validated DailyAttendanceHighLvlStaticQueryReqDTO req, HttpServletResponse resp) {
        dailyAttendanceStaticHighLvlServcie.exportLvlExcel(req,resp);
    }

    @GetMapping("/stu/export")
    @ApiOperation("学生考勤统计导出")
    public void exportStuExcel( @Validated DailyAttendanceStuStaticQueryReqDTO reqDTO, HttpServletResponse resp) {
        dailyAttendanceperiodStuService.exportExcel(reqDTO,resp);
    }

}
