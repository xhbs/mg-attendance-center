package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.AppealPersonListReqDTO;
import com.unisinsight.business.dto.request.AppealStatListReqDTO;
import com.unisinsight.business.dto.response.AppealPersonListResDTO;
import com.unisinsight.business.dto.response.AppealStatListResDTO;
import com.unisinsight.business.service.AppealStatService;
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
 * 申诉统计
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/29
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/appeal-stat")
@Api(tags = "申诉统计")
public class AppealStatController {

    @Resource
    private AppealStatService service;

    @PostMapping("/list")
    @ApiOperation("分页查询")
    public Result<PaginationRes<AppealStatListResDTO>> statByOrg(@RequestBody @Validated AppealStatListReqDTO req) {
        return Result.success(service.statByOrg(req));
    }

    @PostMapping("/export")
    @ApiOperation("导出")
    public void exportByOrg(@RequestBody @Validated AppealStatListReqDTO req, HttpServletResponse resp) {
        service.exportByOrg(req, resp);
    }

    @PostMapping("/list-by-class")
    @ApiOperation("分页查询 - 按学生统计")
    public Result<PaginationRes<AppealPersonListResDTO>> statByPerson(@RequestBody @Validated AppealPersonListReqDTO req) {
        return Result.success(service.statByPerson(req));
    }

    @PostMapping("/export-by-class")
    @ApiOperation("导出 - 按学生统计")
    public void exportByPerson(@RequestBody @Validated AppealPersonListReqDTO req, HttpServletResponse resp) {
        service.exportByPerson(req, resp);
    }
}
