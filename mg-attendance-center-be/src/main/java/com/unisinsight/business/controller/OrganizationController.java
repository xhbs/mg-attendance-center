package com.unisinsight.business.controller;

import com.unisinsight.business.dto.OrgTreeDTO;
import com.unisinsight.business.service.OrganizationService;
import com.unisinsight.framework.common.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName : mg-attendance-center
 * @Description : 组织树
 * @Author : xiehb
 * @Date: 2022/11/10 10:03
 * @Version 1.0.0
 */
@RestController
@RequestMapping("/api/biz-scene/v1/attendance/org")
@Api(tags = "组织")
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;

    @GetMapping("/tree")
    @ApiOperation("组织树")
    public Result<OrgTreeDTO> orgTree() {
        return Result.success(organizationService.tree());
    }
}
