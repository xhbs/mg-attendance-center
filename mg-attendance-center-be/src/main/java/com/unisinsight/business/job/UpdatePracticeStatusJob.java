package com.unisinsight.business.job;

import com.unisinsight.business.mapper.PracticeRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时计算实习状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/1/26
 * @since 1.0
 */
@Component
@Slf4j
public class UpdatePracticeStatusJob implements Runnable {

    @Resource
    private PracticeRecordMapper practiceRecordMapper;

    @Scheduled(cron = "${cron.UpdatePracticeStatusJob}")
    @Override
    public void run() {
        log.info("[定时任务] - 计算实习状态开始 ===>");

        long startedAt = System.currentTimeMillis();
        practiceRecordMapper.calcPracticeStatus();

        log.info("[定时任务] - 计算实习状态，耗时：{}ms <===", System.currentTimeMillis() - startedAt);
    }
}
