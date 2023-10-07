package com.unisinsight.business.controller;

import com.jcraft.jsch.JSchException;
import com.unisinsight.business.job.StuAndOrgSyncJob;
import com.unisinsight.business.job.StudentImageJob;
import com.unisinsight.business.service.impl.StudentImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("stuAndOrgSyncJob")
@Api(tags = "学生和组织同步")
public class StuAndOrgSyncController {

    @Resource
    private StuAndOrgSyncJob stuAndOrgSyncJob;
    @Resource
    private StudentImageJob studentImageJob;

    @GetMapping("/ecoSync")
    @ApiOperation("eco增量同步")
    public void ecoSync() {
        stuAndOrgSyncJob.syncEco();
    }

    @PostMapping("/sync")
    @ApiOperation("同步组织和学生")
    public void sync(@RequestBody Map<String, Object> map) throws JSchException, InterruptedException {
        stuAndOrgSyncJob.run((String) map.get("date"), (Integer) map.get("timeOut"));
    }

    @PostMapping("/syncOrg")
    @ApiOperation("同步组织")
    public void syncOrg(String date) {
        stuAndOrgSyncJob.syncOrgState(date);
    }

    @GetMapping("/syncImageFromAds")
    public void syncImageFromAds() throws JSchException, InterruptedException, SQLException {
        studentImageJob.syncImageFromAds();
    }
}
