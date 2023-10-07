package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.bo.PersonListOfClassBO;
import com.unisinsight.business.bo.SpotTaskResultCountBO;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.dto.response.SpotCheckTaskDTO;
import com.unisinsight.business.job.CallTheRollJob;
import com.unisinsight.business.service.SpotCheckTaskService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 抽查考勤任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/spot-check-task")
@Api(tags = "抽查考勤任务")
public class SpotCheckTaskController {

    @Resource
    private SpotCheckTaskService service;
    @Resource
    private CallTheRollJob callTheRollJob;

    @PostMapping
    @ApiOperation("新建")
    public Result<Integer> add(@RequestBody @Validated SpotCheckTaskAddReqDTO req) {
        return Result.success(service.add(req));
    }

    @PostMapping("/add_call_the_roll")
    @ApiOperation("新建点名")
    public Result<Integer> callTheRoll(@RequestBody SpotCheckTaskAddReqDTO req) {
        return Result.success(service.addCallTheRoll(req));
    }

    @PutMapping
    @ApiOperation("更新")
    public Result update(@RequestBody @Validated SpotCheckTaskUpdateReqDTO req) {
        service.update(req);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询")
    public Result<SpotCheckTaskDTO> get(@PathVariable("id") Integer id) {
        return Result.success(service.get(id));
    }

    @GetMapping("/attendance-dates")
    @ApiOperation("查询考勤日期")
    public Result<List<String>> getAttendanceDates(@RequestParam("id") Integer id) {
        return Result.success(service.getAttendanceDates(id));
    }

    @PostMapping("/list")
    @ApiOperation("分页查询")
    public Result<PaginationRes<SpotCheckTaskDTO>> list(@RequestBody @Validated SpotCheckTaskListReqDTO req) {
        return Result.success(service.list(req));
    }

    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody @Validated @NotEmpty List<Integer> ids) {
        service.delete(ids);
        return Result.success();
    }

    @PostMapping("/call_the_roll")
    @ApiOperation("点名")
    public Result callTheRoll(@RequestBody CallTheRollDto req) {
        service.callTheRoll(req);
        return Result.success();
    }

    @PostMapping("/stu_list")
    @ApiOperation("点名学生列表")
    public Result<PaginationRes<PersonListOfClassBO>> stuList(@RequestBody StuListReq req) {
        return Result.success(service.stuList(req));
    }

    @ApiOperation("学生详情")
    @PostMapping("/stu_detail")
    public Result<PersonListOfClassBO> detail(@RequestBody StuListReq req) {
        return Result.success(service.detail(req));
    }

    @ApiOperation("抽查学生数量")
    @GetMapping("/spot_person_count/{taskId}")
    public Result<SpotTaskResultCountBO> selectSpotPersonCount(@PathVariable int taskId) {
        return Result.success(service.selectSpotPersonCount(taskId));
    }

    @GetMapping("/createSecondCallTheRoll")
    @ApiOperation("手动创建下半月点名")
    public void autoCreateJob() {
        callTheRollJob.secondOfMonth();
    }
}
