package com.unisinsight.business.job;

import com.unisinsight.business.bo.OriginalRecordBO;

import java.time.LocalDate;
import java.util.List;

/**
 * 原始记录消费接口
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/3
 */
public interface OriginalRecordConsumer {

    void consume(LocalDate date, List<OriginalRecordBO> records);
}
