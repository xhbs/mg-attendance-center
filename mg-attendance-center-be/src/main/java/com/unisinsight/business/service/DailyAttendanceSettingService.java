package com.unisinsight.business.service;

import com.unisinsight.business.dto.DailyAttendanceSettingDTO;
import com.unisinsight.business.model.DailyAttendanceSettingDO;

/**
 * 日常考勤设置服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
public interface DailyAttendanceSettingService {

    /**
     * 查询
     */
    DailyAttendanceSettingDO getSetting();

    /**
     * 查询
     */
    DailyAttendanceSettingDTO get();

    /**
     * 更新
     */
    void update(DailyAttendanceSettingDTO req);
}
