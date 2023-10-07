package com.unisinsight.business.job;

import com.unisinsight.business.mapper.LeaveRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时计算请假状态
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/1/26
 * @since 1.0
 */
@Component
@Slf4j
public class UpdateLeaveStateJob implements Runnable {

    @Resource
    private LeaveRecordMapper leaveRecordMapper;

    @Scheduled(cron = "${cron.UpdateLeaveStateJob}")
    @Override
    public void run() {
        log.info("[定时任务] - 计算请假状态开始 ===>");

        long startedAt = System.currentTimeMillis();
        leaveRecordMapper.calcLeaveState();

        log.info("[定时任务] - 计算请假状态，耗时：{}ms <===", System.currentTimeMillis() - startedAt);
    }
}
