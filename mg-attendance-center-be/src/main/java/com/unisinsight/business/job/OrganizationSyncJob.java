package com.unisinsight.business.job;

import com.unisinsight.business.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 同步组织树 定时任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/24
 * @since 1.0
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class OrganizationSyncJob implements ApplicationRunner {

    @Resource
    private OrganizationService organizationService;

    @Scheduled(cron = "${cron.OrganizationSyncJob}")
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("[定时任务] - 同步组织树开始 ===>");

        long startedAt = System.currentTimeMillis();
        organizationService.sync();
        log.info("[定时任务] - 同步组织树完成，耗时：{}ms <===", System.currentTimeMillis() - startedAt);
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(this::run).start();
    }
}
