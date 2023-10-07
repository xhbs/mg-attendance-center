package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.PracticeAttendanceDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendanceListReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonListReqDTO;
import com.unisinsight.business.dto.response.PracticeAttendanceDetailDTO;
import com.unisinsight.business.dto.response.PracticeAttendanceListDTO;
import com.unisinsight.business.dto.response.PracticeAttendancePersonDetailsDTO;
import com.unisinsight.business.dto.response.PracticeAttendancePersonListDTO;
import com.unisinsight.business.service.PracticeAttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 实习点名
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/practice-attendance")
@Api(tags = "实习点名")
public class PracticeAttendanceController {

    @Resource
    private PracticeAttendanceService service;

    @PostMapping("/list")
    @ApiOperation("分页查询")
    public Results<PaginationRes<PracticeAttendanceListDTO>> list(@RequestBody @Validated PracticeAttendanceListReqDTO req) {
        return Results.success(service.list(req));
    }

    @PostMapping("/list-export")
    @ApiOperation("导出")
    public void exportList(@RequestBody @Validated PracticeAttendanceListReqDTO req, HttpServletResponse resp) {
        service.exportList(req, resp);
    }

    @PostMapping("/details")
    @ApiOperation("考勤详情 - 分页查询")
    public Results<PaginationRes<PracticeAttendanceDetailDTO>> details(
            @RequestBody @Validated PracticeAttendanceDetailsReqDTO req) {
        return Results.success(service.details(req));
    }

    @PostMapping("/details-export")
    @ApiOperation("考勤详情 - 导出")
    public void exportDetails(@RequestBody @Validated PracticeAttendanceDetailsReqDTO req, HttpServletResponse resp) {
        service.exportDetails(req, resp);
    }

    @PostMapping("/person-list")
    @ApiOperation("H5 - 点名列表")
    public Results<PaginationRes<PracticeAttendancePersonListDTO>> personList(
            @RequestBody @Validated PracticeAttendancePersonListReqDTO req) {
        return Results.success(service.personList(req));
    }

    @PostMapping("/person-details")
    @ApiOperation("H5 - 点名详情")
    public Results<PracticeAttendancePersonDetailsDTO> personDetails(
            @RequestBody @Validated PracticeAttendancePersonDetailsReqDTO req) {
        return Results.success(service.personDetails(req));
    }

    @GetMapping("/absence-count")
    @ApiOperation("H5 - 获取点名缺勤人数")
    public Results<Integer> getAbsenceCount() {
        return Results.success(service.getAbsenceCount());
    }

    @GetMapping("/mark-as-read")
    @ApiOperation("H5 - 标记缺勤内容已读")
    public void markAsRead(@RequestParam("practice_person_id") Integer practicePersonId) {
        service.markAsRead(practicePersonId);
    }
}
