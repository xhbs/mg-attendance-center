package com.unisinsight.business.controller;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.AttendWeekResultDTO;
import com.unisinsight.business.dto.request.CallTheRollDto;
import com.unisinsight.business.dto.request.QueryStudentListDto;
import com.unisinsight.business.dto.response.*;
import com.unisinsight.business.service.DailyAttendanceService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 日常考勤
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/16
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/daily-attendance")
@Api(tags = "日常考勤 - H5")
public class DailyAttendanceController {

    @Resource
    private DailyAttendanceService service;

    @ApiOperation("H5 - 学生数量查询")
    @GetMapping("/query-count")
    public Result<DailyAttendanceDTO> queryCount() {
        return Result.success(service.countStudents());
    }

    @ApiOperation("H5 - 在籍学生查询")
    @PostMapping("/query-stu")
    public Result<PaginationRes<StudentDTO>> queryStudent(@RequestBody QueryStudentListDto req) {
        return Result.success(service.queryStudent(req));
    }

    @ApiOperation("H5 - 在校/缺勤学生查询(0-在校,99-缺勤)")
    @PostMapping("/query-school")
    public Result<PaginationRes<AttendWeekResultDTO>> queryInSchool(@RequestBody QueryStudentListDto req) {
        return Result.success(service.queryInSchool(req));
    }

    @ApiOperation("H5 - 考勤详情查询")
    @GetMapping("/query-attend")
    public Result<List<InSchoolStuDTO>> queryAttend(@NotNull @RequestParam(value = "person_no") String personNo) {
        return Result.success(service.queryAttend(personNo));
    }

    @ApiOperation("H5 - 请假学生查询")
    @PostMapping("/query-absence")
    public Result<PaginationRes<LeaveStudentDTO>> queryLeave(@RequestBody QueryStudentListDto req) {
        return Result.success(service.queryLeave(req));
    }

    @ApiOperation("H5 - 实习学生查询")
    @PostMapping("/query-practice")
    public Result<PaginationRes<PracticeStudentDTO>> queryPractice(@RequestBody QueryStudentListDto req) {
        return Result.success(service.queryPractice(req));
    }

    @ApiOperation("H5 - 有感考勤")
    @PostMapping("/update-attend")
    public Result updateAttend(@RequestBody CallTheRollDto req) {
        service.updateAttend(req);
        return Result.success();
    }
}
