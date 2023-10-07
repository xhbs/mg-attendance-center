package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.PracticePersonQueryReqDTO;
import com.unisinsight.business.dto.response.PracticePersonDetailDTO;
import com.unisinsight.business.dto.response.PracticePersonListDTO;
import com.unisinsight.business.service.PracticePersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 实习人员统计
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/practice-persons")
@Api(tags = "实习统计")
public class PracticePersonController {

    @Resource
    private PracticePersonService service;

    @GetMapping("/{id}")
    @ApiOperation("查询详情")
    public Results<PracticePersonDetailDTO> get(@PathVariable("id") Integer id) {
        return Results.success(service.get(id));
    }

    @PostMapping
    @ApiOperation("分页查询")
    public Results<PaginationRes<PracticePersonListDTO>> list(@RequestBody PracticePersonQueryReqDTO req) {
        return Results.success(service.list(req));
    }

    @PostMapping("/export")
    @ApiOperation("导出")
    public void export(@RequestBody @Validated PracticePersonQueryReqDTO req, HttpServletResponse resp) {
        service.export(req, resp);
    }
}
