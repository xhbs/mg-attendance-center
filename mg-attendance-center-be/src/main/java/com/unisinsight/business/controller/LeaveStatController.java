package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.LeaveStatListReqDTO;
import com.unisinsight.business.dto.response.LeaveStatListResDTO;
import com.unisinsight.business.service.LeaveStatService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 请假统计
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/leave-stat")
@Api(tags = "请假统计")
public class LeaveStatController {

    @Resource
    private LeaveStatService service;

    @PostMapping("/list")
    @ApiOperation("分页查询")
    public Result<PaginationRes<LeaveStatListResDTO>> list(@RequestBody @Validated LeaveStatListReqDTO req) {
        return Result.success(service.list(req));
    }

    @PostMapping("/export")
    @ApiOperation("导出")
    public void export(@RequestBody @Validated LeaveStatListReqDTO req, HttpServletResponse resp) {
        service.export(req, resp);
    }
}
