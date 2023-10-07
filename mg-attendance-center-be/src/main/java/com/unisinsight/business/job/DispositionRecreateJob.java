package com.unisinsight.business.job;

import com.unisinsight.business.mapper.DispositionRecordMapper;
import com.unisinsight.business.model.DispositionRecord;
import com.unisinsight.business.service.DispositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 重建布控定时任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/21
 */
@Component
@Slf4j
public class DispositionRecreateJob implements Runnable {

    @Resource
    private DispositionService dispositionService;

    @Resource
    private DispositionRecordMapper dispositionRecordMapper;

    /**
     * 定时任务，如果创建或者更新布控任务失败，那么一定时间（60 min）之后重试
     */
    @Scheduled(cron = "0 */60 * * * ?")
    @Override
    public void run() {
        // 查询需要重建的布控列表
        Example example = Example.builder(DispositionRecord.class)
                .where(Sqls.custom()
                        .orEqualTo("dispositionId", "")
                        .orIsNull("dispositionId"))
                .build();

        List<DispositionRecord> records = dispositionRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            log.info("[考勤布控] - 没有需要创建的布控");
        } else {
            List<String> tabIds = records
                    .stream()
                    .map(DispositionRecord::getTabId)
                    .collect(Collectors.toList());
            log.info("[考勤布控] - 需要重建的布控列表: {}", tabIds);
            // 重建布控
            dispositionService.createDispositions(tabIds);
        }
    }
}
