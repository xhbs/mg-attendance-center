package com.unisinsight.business.controller;

import com.unisinsight.business.common.utils.SeetaUtil;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/person_detection")
@Api(tags = "人员信息检测相关接口")
public class PersonDetectionController {

    @Resource
    private SeetaUtil seetaUtil;

    @GetMapping("/live_detection")
    @ApiOperation("图片静默活体检测")
    public Result<Boolean> liveDetection(@RequestParam MultipartFile file) throws Exception {
        return Result.success(seetaUtil.liveDetection(file));
    }
}
