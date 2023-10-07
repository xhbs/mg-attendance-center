package com.unisinsight.business.controller;

import com.unisinsight.business.service.impl.StudentImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/mdk")
@Api("学生照片同步相关接口")
public class StudentMdkController {

    @Resource
    private StudentImageService studentImageService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/import")
    @ApiOperation("手动执行导入学生图片代码")
    public void importMdk() throws Exception {
        studentImageService.importStudentImage();
    }

    @GetMapping("/download-database")
    @ApiOperation("下载图片")
    public void downloadImageInDatabase() throws Exception {
        studentImageService.downloadImageInDatabase();
    }

    @GetMapping("/statistics")
    public void statistics() throws Exception {
        studentImageService.statistics();
    }

    @GetMapping("/delete-all")
    public void deleteAll() throws Exception {
        studentImageService.deleteAllMdk();
    }

    @GetMapping("/cleanRedis")
    @ApiOperation("删除 导入图片相关的 rediskey,传null删除全部,否则删除对应的key")
    public void deleteRedis(String key) {
        if (key.equals("null")) {
            stringRedisTemplate.delete("mdk_name_key");
            stringRedisTemplate.delete("save_success_key");
            stringRedisTemplate.delete("register_success_key");
            stringRedisTemplate.delete("mdk_save");
            stringRedisTemplate.delete("upload_success_key");
        } else {
            stringRedisTemplate.delete(key);
        }
    }
}
