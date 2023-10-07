package com.unisinsight.business.controller;

import com.unisinsight.business.dto.DailyAttendanceSettingDTO;
import com.unisinsight.business.service.DailyAttendanceSettingService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 日常考勤设置
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/daily-attendance-setting")
@Api(tags = "日常考勤设置")
public class DailyAttendanceSettingController {

    @Resource
    private DailyAttendanceSettingService service;

    @ApiOperation("查询")
    @GetMapping
    public Result<DailyAttendanceSettingDTO> get() {
        return Result.success(service.get());
    }

    @ApiOperation("更新")
    @PostMapping
    public Result update(@RequestBody @Validated DailyAttendanceSettingDTO req) {
        service.update(req);
        return Result.success();
    }
}
