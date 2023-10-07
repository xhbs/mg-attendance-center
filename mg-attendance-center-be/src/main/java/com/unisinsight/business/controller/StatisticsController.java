package com.unisinsight.business.controller;

import com.unisinsight.business.dto.*;
import com.unisinsight.business.dto.request.StatisticsProvinceReq;
import com.unisinsight.business.dto.request.StatisticsReq;
import com.unisinsight.business.service.StatisticsService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName : mg-attendance-center
 * @Description : 统计
 * @Author : xiehb
 * @Date: 2022/11/04 15:04
 * @Version 1.0.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/statistics")
@Api(tags = "统计")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @ApiOperation("省直属/地州 学校统计")
    @PostMapping("/province")
    public Result<StatisticsProvince> provinceStatistics(@RequestBody StatisticsProvinceReq statisticsReq) {
        return Result.success(statisticsService.provinceStatistics(statisticsReq));
    }

    @ApiOperation("省内所有学校前五名考勤率")
    @PostMapping("/all-school-top-5")
    public Result<List<StatisticsAllSchool>> allSchoolStatistics(@RequestBody StatisticsReq statisticsReq) {
        return Result.success(statisticsService.allSchoolStatistics(statisticsReq));
    }

    @ApiOperation("学校分布情况")
    @GetMapping("/school-count")
    public Result<SchoolNumStatisticsRes> schoolCount() {
        return Result.success(statisticsService.schoolNumberStatistics());
    }

    @ApiOperation("省直属学校学期学生在校率")
    @PostMapping("/province-term")
    public Result<List<TermStatisticsProvince>> provinceTermStatistics(@RequestBody StatisticsReq req) throws InterruptedException {
        return Result.success(statisticsService.statisticsTerm(req));
    }

    @ApiOperation("校级考勤统计")
    @PostMapping("/school")
    public Result<StatisticsProvince> schoolStatistics(@RequestBody StatisticsReq req) {
        return Result.success(statisticsService.schoolStatistics(req));
    }

    @ApiOperation("市级考勤统计")
    @PostMapping("/city")
    public Result<StatisticsProvince> cityStatistics(@RequestBody StatisticsReq req) {
        return Result.success(statisticsService.cityStatistics(req));
    }

    @ApiOperation("市级学校考勤top5")
    @PostMapping("/city-top5")
    public Result<List<StatisticsAllSchool>> citySchoolTop5Statistics(@RequestBody StatisticsReq req) {
        return Result.success(statisticsService.citySchoolTop5Statistics(req));
    }

    @ApiOperation("市级学校 1学期20个周期 统计")
    @PostMapping("/city-term")
    public Result<List<TermStatisticsProvince>> cityTerm(@RequestBody StatisticsReq req) throws InterruptedException {
        return Result.success(statisticsService.cityStatisticsTerm(req));
    }

    @ApiOperation("校级 班级top5")
    @PostMapping("/school-class-top5")
    public Result<List<StatisticsAllSchool>> classTop5(@RequestBody StatisticsReq req) throws InterruptedException {
        return Result.success(statisticsService.classTop5(req));
    }

    @ApiOperation("校级 1学期20个周期 统计")
    @PostMapping("/school-term")
    public Result<List<TermStatisticsProvince>> schoolTerm(@RequestBody StatisticsReq req) throws InterruptedException {
        return Result.success(statisticsService.schoolStatisticsTerm(req));
    }

    @ApiOperation("省内各个城市的考勤统计")
    @PostMapping("/province-2-every-city")
    public Result<List<StatisticsEvery>> province2EveryCity(@RequestBody StatisticsReq req) throws InterruptedException {
        return Result.success(statisticsService.statisticsProvince2City(req));
    }

    @ApiOperation("市内的各个学校考勤统计")
    @PostMapping("/city-2-every-school")
    public Result<List<StatisticsEvery>> city2EverySchool(@RequestBody StatisticsReq req) throws InterruptedException {
        return Result.success(statisticsService.statisticsCity2School(req));
    }
}
