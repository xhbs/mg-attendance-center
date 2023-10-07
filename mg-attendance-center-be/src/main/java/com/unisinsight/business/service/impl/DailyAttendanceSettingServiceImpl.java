package com.unisinsight.business.service.impl;

import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.DailyAttendanceSettingDTO;
import com.unisinsight.business.mapper.DailyAttendanceSettingMapper;
import com.unisinsight.business.model.DailyAttendanceSettingDO;
import com.unisinsight.business.service.DailyAttendanceSettingService;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 日常考勤设置服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@Slf4j
@Service
public class DailyAttendanceSettingServiceImpl implements DailyAttendanceSettingService {

    @Resource
    private DailyAttendanceSettingMapper dailyAttendanceSettingMapper;

    @Override
    public DailyAttendanceSettingDO getSetting() {
        return dailyAttendanceSettingMapper.selectOne(new DailyAttendanceSettingDO());
    }

    @Override
    public DailyAttendanceSettingDTO get() {
        DailyAttendanceSettingDO settings = dailyAttendanceSettingMapper.selectOne(new DailyAttendanceSettingDO());
        if (settings == null) {
            throw new BaseException("日常考勤设置已被删除");
        }

        DailyAttendanceSettingDTO res = new DailyAttendanceSettingDTO();
        BeanUtils.copyProperties(settings, res);
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(DailyAttendanceSettingDTO req) {
        DailyAttendanceSettingDO setting = new DailyAttendanceSettingDO();
        BeanUtils.copyProperties(req, setting);

        User user = UserUtils.mustGetUser();
        setting.setUpdatedBy(user.getUserCode());
        setting.setUpdatedAt(LocalDateTime.now());

        Example example = Example.builder(DailyAttendanceSettingDO.class).build();
        dailyAttendanceSettingMapper.updateByCondition(setting, example);
        log.info("{} 更新了日常考勤配置：{}", user.getUserCode(), setting);
    }
}
