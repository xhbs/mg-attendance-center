package com.unisinsight.business.controller;

import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.dto.request.FaceMatchReqDTO;
import com.unisinsight.business.service.FaceMatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 人脸比对接口
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/result")
@Api(tags = "人脸比对")
public class FaceMatchController {

    @Resource
    private FaceMatchService service;

    @PostMapping("/face-match")
    @ApiOperation("人脸比对")
    public Results faceMatch(@RequestBody @Validated FaceMatchReqDTO req) throws Exception {
        service.faceMatch(req);
        return Results.success();
    }
}
