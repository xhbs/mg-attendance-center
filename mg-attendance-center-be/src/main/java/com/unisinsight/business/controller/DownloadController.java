package com.unisinsight.business.controller;

import com.unisinsight.business.service.DownloadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Excel模板下载
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/download")
@Api(tags = "文件下载")
public class DownloadController {

    @Resource
    private DownloadService service;

    @GetMapping("/excel-tpl/appeal-name-list")
    @ApiOperation("申诉名单模板")
    public void appeal(HttpServletResponse resp) {
        service.download("appeal_persons.xlsx", "申诉名单模板.xlsx", resp);
    }
}
