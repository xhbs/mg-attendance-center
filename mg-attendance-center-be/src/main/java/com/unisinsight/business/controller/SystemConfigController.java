package com.unisinsight.business.controller;

import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.SystemConfigDTO;
import com.unisinsight.business.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统配置
 *
 * @author wangyi01
 * @date 2020/7/16
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/system-configs")
@Api(tags = "系统配置")
@ApiSort(1)
public class SystemConfigController {

    @Resource
    private SystemConfigService service;

    @ApiOperation(value = "查询")
    @GetMapping
    public Results<List<SystemConfigDTO>> getFrontendConfigs() {
        return Results.success(service.getFrontendConfigs());
    }
}
