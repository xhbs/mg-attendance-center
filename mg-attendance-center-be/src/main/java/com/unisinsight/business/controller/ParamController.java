package com.unisinsight.business.controller;

import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/biz-scene/v1/attendance/param")
@Api("获取配置文件参数")
public class ParamController {

    @Value("${test.open-calltheroll}")
    private Boolean openCallTheRoll;

    @ApiOperation("是否放开点名测试")
    @GetMapping("/open_call_the_roll_test")
    public Result<Boolean> openCallTheRollTest() {
        return com.unisinsight.framework.common.base.Result.success(openCallTheRoll);
    }
}
