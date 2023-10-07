package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.request.LeaveAddReqDTO;
import com.unisinsight.business.dto.request.LeaveRecordQueryReqDTO;
import com.unisinsight.business.dto.request.LeaveUpdateReqDTO;
import com.unisinsight.business.dto.response.LeaveRecordDetailDTO;
import com.unisinsight.business.dto.response.LeaveRecordListDTO;
import com.unisinsight.business.service.LeaveRecordService;
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
 * 请假管理
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/leave-records")
@Api(tags = "请假管理")
public class LeaveRecordController {

    @Resource
    private LeaveRecordService service;

    @PostMapping("/add")
    @ApiOperation("新增")
    public Results add(@RequestBody @Validated LeaveAddReqDTO req) {
        service.add(req);
        return Results.success();
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    @ApiOperation("修改")
    public Results update(@PathVariable("id") Integer id, @RequestBody @Validated LeaveUpdateReqDTO req) {
        service.update(id, req);
        return Results.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询详情")
    public Results<LeaveRecordDetailDTO> get(@PathVariable("id") Integer id) {
        return Results.success(service.get(id));
    }

    @PostMapping
    @ApiOperation("分页查询")
    public Results<PaginationRes<LeaveRecordListDTO>> list(@RequestBody @Validated LeaveRecordQueryReqDTO req) {
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
