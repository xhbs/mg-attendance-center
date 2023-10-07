package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.AppealAddReqDTO;
import com.unisinsight.business.dto.request.AppealRecordQueryReqDTO;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.response.AppealRecordDetailDTO;
import com.unisinsight.business.dto.response.AppealRecordListDTO;
import com.unisinsight.business.service.AppealRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考勤申诉记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11 11:23:53
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/appeal-records")
@Api(tags = "考勤申诉")
public class AppealRecordController {

    @Resource
    private AppealRecordService service;

    @PostMapping("/add")
    @ApiOperation("新增申诉")
    public Results add(@RequestBody @Validated AppealAddReqDTO req) {
        service.add(req);
        return Results.success();
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    @ApiOperation("修改申诉")
    public Results update(@PathVariable("id") Integer id, @RequestBody @Validated AppealAddReqDTO req) {
        service.update(id, req);
        return Results.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询详情")
    public Results<AppealRecordDetailDTO> get(@PathVariable("id") Integer id) {
        return Results.success(service.get(id));
    }

    @PostMapping
    @ApiOperation("分页查询")
    public Results<PaginationRes<AppealRecordListDTO>> list(@RequestBody @Validated AppealRecordQueryReqDTO req) {
        return Results.success(service.list(req));
    }

    @RequestMapping(path = "/appeal", method = {RequestMethod.PUT, RequestMethod.POST})
    @ApiOperation("批量申诉")
    public Results appeal(@RequestBody List<Integer> ids) {
        service.appeal(ids);
        return Results.success();
    }

    @RequestMapping(path = "/approval", method = {RequestMethod.PUT, RequestMethod.POST})
    @ApiOperation("批量处理")
    public Results approval(@RequestBody @Validated ApprovalReqDTO req) {
        service.approval(req);
        return Results.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除")
    public Results delete(@RequestParam(value = "ids") String ids) {
        if (StringUtils.isEmpty(ids)) {
            throw new InvalidParameterException("ID为空");
        }

        service.delete(Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList()));
        return Results.success();
    }

    @PostMapping("/delete")
    @ApiOperation("批量删除")
    public Results _delete(@RequestParam(value = "ids") String ids) {
        return delete(ids);
    }

    @PostMapping("/export")
    @ApiOperation("导出")
    public void export(@RequestBody @Validated AppealRecordQueryReqDTO req, HttpServletResponse resp) {
        service.export(req, resp);
    }
}
