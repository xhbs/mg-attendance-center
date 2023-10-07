package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.DailyAttendanceExcludeDateDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateAddReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateQueryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateUpdateReqDTO;
import com.unisinsight.business.service.DailyAttendanceExcludeDateService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 日常考勤排除日期设置
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/daily-exclude-date")
@Api(tags = "日常考勤 - 节假日设置")
@Slf4j
public class DailyAttendanceExcludeDateController {

    @Resource
    private DailyAttendanceExcludeDateService service;

    /**
     * 添加
     */
    @ApiOperation("添加")
    @PostMapping
    public Result add(@RequestBody @Validated DailyAttendanceExcludeDateAddReqDTO req) {
        service.add(req);
        return Result.success();
    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PutMapping
    public Result update(@RequestBody @Validated DailyAttendanceExcludeDateUpdateReqDTO req) {
        service.update(req);
        return Result.success();
    }

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping
    public Result<PaginationRes<DailyAttendanceExcludeDateDTO>> query(@Validated DailyAttendanceExcludeDateQueryReqDTO req) {
        return Result.success(service.query(req));
    }

    /**
     * 批量删除
     */
    @ApiOperation("批量删除")
    @DeleteMapping
    public Result batchDelete(@RequestBody @Validated @NotNull @NotEmpty List<Integer> ids) {
        service.batchDelete(ids);
        return Result.success();
    }
}
