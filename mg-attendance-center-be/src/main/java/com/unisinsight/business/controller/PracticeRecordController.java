package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.PracticeAddReqDTO;
import com.unisinsight.business.dto.request.PracticeRecordQueryReqDTO;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.response.PracticeRecordDetailDTO;
import com.unisinsight.business.dto.response.PracticeRecordListDTO;
import com.unisinsight.business.service.PracticeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实习管理
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/practice-records")
@Api(tags = "实习管理")
public class PracticeRecordController {

    @Resource
    private PracticeRecordService service;

    @PostMapping("/add")
    @ApiOperation("新增")
    public Results add(@RequestBody @Validated PracticeAddReqDTO req) {
        service.add(req);
        return Results.success();
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    @ApiOperation("修改")
    public Results update(@PathVariable("id") Integer id, @RequestBody @Validated PracticeAddReqDTO req) {
        service.update(id, req);
        return Results.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询详情")
    public Results<PracticeRecordDetailDTO> get(@PathVariable("id") Integer id) {
        return Results.success(service.get(id));
    }

    @PostMapping
    @ApiOperation(value = "分页查询")
    public Results<PaginationRes<PracticeRecordListDTO>> list(@RequestBody @Validated PracticeRecordQueryReqDTO req) {
        return Results.success(service.list(req));
    }

    @RequestMapping(path = "/report", method = {RequestMethod.PUT, RequestMethod.POST})
    @ApiOperation("批量上报")
    public Results report(@RequestBody List<Integer> ids) {
        service.report(ids);
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
}

