package com.unisinsight.business.common.utils;

import cn.hutool.core.util.StrUtil;
import com.unisinsight.business.bo.DateRangeBO;
import com.unisinsight.framework.common.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * 根据学年、学期、月份确定时间范围
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/2
 */
public final class DateRangeCalcUtil {
    public static DateRangeBO calc(String schoolYear, String semester, String month) {
        return calc(schoolYear, semester, month, null);
    }

    /**
     * 根据学年、学期、月份 确定查询的考勤周期
     */
    public static DateRangeBO calc(String schoolYear, String semester, String month, String day) {
        if (StringUtils.isNotEmpty(month)) {
            // 传了月份，查询本月数据
            try {
                LocalDate firstDay = LocalDate.parse(month + "-" + (StrUtil.isNotBlank(day) ? day : "01"), DateUtils.DATE_FORMATTER);
                LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());
                return new DateRangeBO(firstDay, lastDay);
            } catch (Exception e) {
                throw new InvalidParameterException("月份参数错误");
            }
        }

        // 不限月份，根据学年和学期确定日期
        String[] split = schoolYear.split("-");
        if (split.length != 2) {
            throw new InvalidParameterException("学年参数错误");
        }

        int startYear = Integer.parseInt(split[0]);
        int endYear = Integer.parseInt(split[1]);
        if (startYear >= endYear) {
            throw new InvalidParameterException("学年参数错误");
        }

        // 秋季学期日期为当年9.1～次年1.31；春季学期日期为次年3.1～次年7.31
        if ("春季".equals(semester)) {
            return new DateRangeBO(LocalDate.of(endYear, 3, 1),
                    LocalDate.of(endYear, 7, 31));
        } else if ("秋季".equals(semester)) {
            return new DateRangeBO(LocalDate.of(startYear, 9, 1),
                    LocalDate.of(endYear, 1, 31));
        } else {
            // 不限学期
            return new DateRangeBO(LocalDate.of(startYear, 9, 1),
                    LocalDate.of(endYear, 7, 31));
        }
    }
}
