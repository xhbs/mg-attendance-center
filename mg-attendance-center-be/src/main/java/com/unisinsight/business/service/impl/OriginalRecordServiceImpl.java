/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service.impl;

import com.unisinsight.business.mapper.OriginalRecordMapper;
import com.unisinsight.business.model.OriginalRecordDO;
import com.unisinsight.business.service.OriginalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 原始数据Service
 *
 * @author chengnian [cheng.nian@unisinsight.com]
 * @date 2020/09/03 12:36:09
 * @since 1.0
 */
@Service
@Slf4j
public class OriginalRecordServiceImpl implements OriginalRecordService {

    @Resource
    private OriginalRecordMapper originalRecordMapper;

    @Override
    public void batchSave(List<OriginalRecordDO> list) {
        long startTime = System.currentTimeMillis();
        originalRecordMapper.batchSave(list);
        log.info("生成 {} 条原始记录，耗时 {} 毫秒", list.size(), System.currentTimeMillis() - startTime);
    }
}
