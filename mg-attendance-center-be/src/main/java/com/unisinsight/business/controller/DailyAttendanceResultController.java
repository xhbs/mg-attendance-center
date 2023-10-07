package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.DailyAttendanceResultDTO;
import com.unisinsight.business.dto.request.AtsHistoryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceDetailQueryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceDetailReqDTO;
import com.unisinsight.business.dto.response.AtsHistoryResDTO;
import com.unisinsight.business.dto.response.DailyAttendanceDetailResDTO;
import com.unisinsight.business.service.DailyAttendanceResultService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 日常考勤明细查询
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance")
@Api(tags = "日常考勤 - 学生明细")
public class DailyAttendanceResultController {

    @Resource
    private DailyAttendanceResultService service;

    /**
     * 分页查询
     */
    @ApiOperation("分页查询个人考勤明细")
    @GetMapping("/daily-results")
    public Result<PaginationRes<DailyAttendanceResultDTO>> query(@Validated DailyAttendanceDetailQueryReqDTO req) {
        return Result.success(service.query(req));
    }

    @ApiOperation("查询详情")
    @GetMapping("/daily-results/details")
    public Result<DailyAttendanceDetailResDTO> getDetails(@Validated DailyAttendanceDetailReqDTO req) {
        return Result.success(service.getDetails(req));
    }

    /**
     * 根据人员查询历史考勤
     */
    @ApiOperation("查询历史考勤")
    @GetMapping("/result/history")
    public Results<AtsHistoryResDTO> getHistoryByPerson(@Validated AtsHistoryReqDTO req) {
        return Results.success(service.getHistoryByPerson(req));
    }
}
