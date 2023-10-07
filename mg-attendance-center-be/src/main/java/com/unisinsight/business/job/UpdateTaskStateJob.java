package com.unisinsight.business.job;

import com.unisinsight.business.mapper.SpotCheckTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时计算抽查任务状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@Component
@Slf4j
public class UpdateTaskStateJob implements Runnable {

    @Resource
    private SpotCheckTaskMapper spotCheckTaskMapper;

    @Scheduled(cron = "${cron.UpdateTaskStateJob}")
    @Override
    public void run() {
        log.info("[定时任务] - 计算抽查任务状态开始 ===>");

        long startedAt = System.currentTimeMillis();
        spotCheckTaskMapper.updateState();

        log.info("[定时任务] - 计算抽查任务状态，耗时：{}ms <===", System.currentTimeMillis() - startedAt);
    }
}
