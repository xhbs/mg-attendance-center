package com.unisinsight.business.service;

import java.util.List;

/**
 * 考勤事件过滤器
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/3/4
 * @since 1.0
 */
public interface AttendanceEventFilter {
    /**
     * 刷新人员缓存
     */
    void refreshPersonCache();

    /**
     * 刷新通道缓存
     */
    void refreshChannelCache();

    /**
     * 添加缓存
     */
    void addPersonCache(List<String> personNos);

    /**
     * 移除缓存
     */
    void removePersonCache(List<String> personNos);

    /**
     * 校验考勤事件是否有效
     *
     * @return 如果事件有效返回 true
     */
    boolean checkAttendanceEvent(String personNo, String deviceCode, long eventTime);
}
