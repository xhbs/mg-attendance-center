package com.unisinsight.business.controller;

import cn.hutool.log.Log;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.dto.response.*;
import com.unisinsight.business.service.SpotCheckAttendanceService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 抽查考勤
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/spot-check-attend")
@Api(tags = "抽查考勤")
@Slf4j
public class SpotCheckAttendanceController {

    @Value("${unisinsight.headerLog}")
    private Boolean headerLog;
    @Resource
    private SpotCheckAttendanceService service;

    @PostMapping("/count")
    @ApiOperation("抽查考勤统计")
    public Result<PaginationRes<TaskResultStatResDTO>> statistics(@RequestBody @Validated TaskResultStatReqDTO req) {
        return Result.success(service.statistics(req));
    }

    @PostMapping("/list")
    @ApiOperation("抽查考勤明细")
    public Result<PaginationRes<TaskResultListResDTO>> list(@RequestBody @Validated TaskResultListReqDTO req) {
        return Result.success(service.list(req));
    }

    @ApiOperation("抽查考勤详情")
    @GetMapping("/details")
    public Result<SpotAttendanceDetailResDTO> getDetails(@Validated SpotAttendanceDetailReqDTO req) {
        return Result.success(service.getDetails(req));
    }

    @ApiOperation("抽查考勤统计excel导出")
    @GetMapping("/export-count")
    public void exportCountExcel(@Validated TaskResultStatExportReqDTO req, HttpServletResponse resp) {
        service.exportCountExcel(req, resp);
    }

    @ApiOperation("抽查考勤明细excel导出")
    @GetMapping("/export-list")
    public void exportListExcel(@Validated TaskResultListExportReqDTO req, HttpServletResponse resp) {
        service.exportListExcel(req, resp);
    }

    @PostMapping("/check-num")
    @ApiOperation("抽查考勤任务数量")
    public Result<SpotCheckNumDTO> checkNum(HttpServletRequest request) {
        if (headerLog) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                //根据名称获取请求头的值
                String value = request.getHeader(name);
                log.info("请求头:{}：{}", name, value);
            }
        }
        return Result.success(service.checkNum());
    }

    @PostMapping("/check-list")
    @ApiOperation("抽查列表查询")
    public Result<PaginationRes<SpotCheckAttendListDTO>> checkList(@RequestBody @Validated SpotCheckAttendListReqDTO reqDTO) {
        return Result.success(service.getTasksOfClasses(reqDTO));
    }

    @PostMapping("/attend-list")
    @ApiOperation("有感考勤列表查询")
    public Result<PaginationRes<SpotCheckAttendDetailDTO>> attendList(@RequestBody @Validated SpotCheckAttendDetailReqDTO reqDTO) {
        return Result.success(service.getHaveTaskStudents(reqDTO));
    }

    @PostMapping("/attend-detail")
    @ApiOperation("有感考勤详情")
    public Result<FeelAttendDetailDTO> attendDetail(@RequestBody @Validated FeelAttendDetailReqDTO reqDTO) {
        return Result.success(service.attendDetail(reqDTO));
    }
}
