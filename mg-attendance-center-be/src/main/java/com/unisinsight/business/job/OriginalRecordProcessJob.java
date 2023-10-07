package com.unisinsight.business.job;

import com.unisinsight.business.bo.OriginalRecordBO;
import com.unisinsight.business.mapper.OriginalRecordMapper;
import com.unisinsight.business.model.SystemConfigDO;
import com.unisinsight.business.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消费原始打卡记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
@Component
@Slf4j
public class OriginalRecordProcessJob implements ApplicationRunner {

    /**
     * 将最近处理的原始记录ID持久化到系统配置表
     */
    private static final String LATEST_PROCESSED_ORIGINAL_RECORD_ID_KEY = "latest-processed-original-record-id";

    /**
     * 最近处理的原始记录ID
     */
    private Long latestProcessedRecordId;

    /**
     * 最多处理多少天前的数据
     */
    @Value("${attendance-result-handler.result-handle-max-interval-days:7}")
    private int resultHandleMaxIntervalDays;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private OriginalRecordMapper originalRecordMapper;

    @Resource
    private DailyAttendanceGenerateJob dailyAttendanceGenerateJob;

    @Resource
    private SpotAttendanceGenerateJob spotAttendanceGenerateJob;

    @Resource
    private PracticeAttendanceGenerateJob practiceAttendanceGenerateJob;

    /**
     * 从数据库查最后一次处理的原始数据ID
     */
    private void initLatestProcessedRecordId() {
        latestProcessedRecordId = systemConfigService.getLongConfigValue(LATEST_PROCESSED_ORIGINAL_RECORD_ID_KEY);
        log.info("获取最后处理的原始记录ID： {}", latestProcessedRecordId);

        if (latestProcessedRecordId == null) {
            SystemConfigDO config = new SystemConfigDO();
            config.setKey(LATEST_PROCESSED_ORIGINAL_RECORD_ID_KEY);
            config.setValue("0");
            config.setCreatedAt(LocalDateTime.now());
            config.setDescribe("最后处理的原始记录ID");
            systemConfigService.add(config);
        }
    }

    /**
     * 更新最后一次处理的原始数据ID
     */
    private void updateLatestProcessedRecordId(Long id) {
        systemConfigService.updateValue(LATEST_PROCESSED_ORIGINAL_RECORD_ID_KEY, String.valueOf(id));
        latestProcessedRecordId = id;
    }

    @Override
    public void run(ApplicationArguments args) {
        initLatestProcessedRecordId();
        new Thread(this::run).start();
    }

    private void run() {
        OriginalRecordConsumer[] consumers = new OriginalRecordConsumer[]{
                dailyAttendanceGenerateJob,
                spotAttendanceGenerateJob,
                practiceAttendanceGenerateJob
        };

        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 查询最近一段时间未处理的数据
                LocalDateTime timeLimit = LocalDateTime.now().minusDays(resultHandleMaxIntervalDays);
                List<OriginalRecordBO> records = originalRecordMapper.fetchUnProcessRecordOrderly(
                        latestProcessedRecordId, timeLimit, 1000);
                if (records.isEmpty()) {
                    log.info("暂无未处理原始数据，睡眠一段时间后再次获取，latest_processed_record_id: {}",
                            latestProcessedRecordId);
                    // 睡眠一段时间后再次尝试查询
                    try {
                        Thread.sleep(60_000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // 根据日期分组
                    long startedAt = System.currentTimeMillis();
                    log.info("开始处理 {} 条原始数据，===>", records.size());

                    records.stream()
                            .collect(Collectors.groupingBy(OriginalRecordBO::getAttendanceDate))
                            .forEach((date, _originalRecords) -> {
                                // 批量生成日常考勤和抽查考勤结果
                                for (OriginalRecordConsumer consumer : consumers) {
                                    consumer.consume(date, _originalRecords);
                                }
                            });
                    log.info("完成处理 {} 条原始数据，耗时：{} ms <===", records.size(),
                            System.currentTimeMillis() - startedAt);

                    updateLatestProcessedRecordId(records.get(records.size() - 1).getId());
                }
            } catch (Exception e) {
                log.error("处理原始数据异常", e);

                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
