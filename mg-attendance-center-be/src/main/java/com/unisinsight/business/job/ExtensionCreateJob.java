package com.unisinsight.business.job;

import com.unisinsight.business.mapper.CommonTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动时创建 pgsql 插件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/3/1
 * @since 1.0
 */
@Component
@Slf4j
public class ExtensionCreateJob implements ApplicationRunner {
    @Resource
    private CommonTableMapper commonTableMapper;

    @Override
    public void run(ApplicationArguments args) {
        commonTableMapper.createExtensions(new String[]{"pg_trgm", "dblink"});
    }
}
