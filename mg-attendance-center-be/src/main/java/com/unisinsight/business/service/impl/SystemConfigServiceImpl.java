package com.unisinsight.business.service.impl;

import com.unisinsight.business.common.constants.SystemConfigs;
import com.unisinsight.business.dto.SystemConfigDTO;
import com.unisinsight.business.mapper.SystemConfigMapper;
import com.unisinsight.business.model.SystemConfigDO;
import com.unisinsight.business.service.SystemConfigService;
import com.unisinsight.framework.common.util.user.User;
import com.unisinsight.framework.common.util.user.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/3/24
 * @since 1.0
 */
@Service
@Slf4j
@DependsOn("flywayInitializer")
public class SystemConfigServiceImpl implements SystemConfigService {
    @Resource
    private SystemConfigMapper systemConfigMapper;

    @PostConstruct
    public void setDefaultConfigs() {
        // 默认配置项
        List<SystemConfigDO> defaultConfigs = new ArrayList<>();

        // 秋季学期开始日期
        defaultConfigs.add(new SystemConfigDO(SystemConfigs.AUTUMN_SCHOOL_OPEN_DAY, "09-01", "秋季学期开始日期"));
        // 秋季学期结束日期
        defaultConfigs.add(new SystemConfigDO(SystemConfigs.AUTUMN_SCHOOL_CLOSE_DAY, "01-31", "秋季学期结束日期"));
        // 春季学期开始日期
        defaultConfigs.add(new SystemConfigDO(SystemConfigs.SPRING_SCHOOL_OPEN_DAY, "03-01", "春季学期开始日期"));
        // 春季学期结束日期
        defaultConfigs.add(new SystemConfigDO(SystemConfigs.SPRING_SCHOOL_CLOSE_DAY, "07-31", "春季学期结束日期"));

        // 考勤申诉最大有效天数
        defaultConfigs.add(new SystemConfigDO(SystemConfigs.MAXIMUM_APPEAL_PERIOD_DAYS, "7", "考勤申诉最大有效天数"));
        // 需要提前多少天发起实习申请
        defaultConfigs.add(new SystemConfigDO(SystemConfigs.PRACTICE_SUBMIT_ADVANCE_DAYS, "14", "需要提前多少天发起实习申请"));
        // 实习点名考勤任务天数
        defaultConfigs.add(new SystemConfigDO(SystemConfigs.PRACTICE_ATTENDANCE_DAY_COUNT, "7", "实习点名考勤任务天数"));

        LocalDateTime now = LocalDateTime.now();
        for (SystemConfigDO config : defaultConfigs) {
            config.setCreatedAt(now);
            config.setCreatedBy("admin");
        }
        systemConfigMapper.saveConfigs(defaultConfigs);
        log.info("[系统配置] - 设置系统默认配置");
    }

    @Override
    public void add(SystemConfigDO config) {
        User user = UserHandler.getUser();
        if (user == null) {
            config.setCreatedBy("admin");
        } else {
            config.setCreatedBy(user.getUserCode());
        }
        config.setCreatedAt(LocalDateTime.now());
        systemConfigMapper.insertSelective(config);
        log.info("{} 新增了配置：{}", user == null ? "admin" : user.getUserCode(), config);
    }

    @Override
    public void updateValue(String key, String value) {
        SystemConfigDO config = new SystemConfigDO();
        config.setKey(key);
        config.setValue(value);
        config.setUpdatedAt(LocalDateTime.now());

        User user = UserHandler.getUser();
        if (user == null) {
            config.setUpdatedBy("admin");
        } else {
            config.setUpdatedBy(user.getUserCode());
        }

        systemConfigMapper.updateByPrimaryKeySelective(config);
        // log.info("{} 更新了配置，key：{} value：{}", user == null ? "admin" : user.getUserCode(), key, value);
    }

    @Override
    public String getConfigValue(String key) {
        SystemConfigDO config = systemConfigMapper.selectByPrimaryKey(key);
        if (config != null) {
            return config.getValue();
        }
        return null;
    }

    @Override
    public Integer getIntegerConfigValue(String key) {
        String value = getConfigValue(key);
        if (Strings.isNotEmpty(value)) {
            return Integer.parseInt(value);
        }
        return null;
    }

    @Override
    public Long getLongConfigValue(String key) {
        String value = getConfigValue(key);
        if (Strings.isNotEmpty(value)) {
            return Long.parseLong(value);
        }
        return null;
    }

    @Override
    public List<SystemConfigDTO> getFrontendConfigs() {
        List<String> keys = Arrays.asList(SystemConfigs.PRACTICE_SUBMIT_ADVANCE_DAYS,
                SystemConfigs.MAXIMUM_APPEAL_PERIOD_DAYS);
        Example example = Example.builder(SystemConfigDO.class)
                .where(Sqls.custom()
                        .andIn("key", keys))
                .build();
        return systemConfigMapper.selectByCondition(example)
                .stream()
                .map(src -> {
                    SystemConfigDTO dst = new SystemConfigDTO();
                    dst.setKey(src.getKey());
                    dst.setValue(src.getValue());
                    dst.setDescribe(src.getDescribe());
                    return dst;
                })
                .collect(Collectors.toList());
    }
}
