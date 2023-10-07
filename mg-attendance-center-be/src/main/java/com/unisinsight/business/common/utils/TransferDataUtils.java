package com.unisinsight.business.common.utils;

import com.unisinsight.business.bo.StaticDateBO;
import com.unisinsight.business.common.constants.SystemConfigs;
import com.unisinsight.business.dto.WeekDay;
import com.unisinsight.business.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.core.Local;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * @author tanggang
 * @version 1.0
 * @email tang.gang@inisinsight.com
 * @date 2021/8/17 10:06
 **/
@Component
@Slf4j
public class TransferDataUtils implements InitializingBean {

    private String autumnSchoolOpenDay;

    private String autumnSchoolCloseDay;

    private String springSchoolOpenDay;

    private String springSchoolCloseDay;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * @param
     * @return
     * @description 初始化开学放假时间
     * @date 2021/8/27 10:18
     */
    private void initSchoolDay() {
        if (StringUtils.isNotEmpty(springSchoolCloseDay)) {//判断是否初始化
            return;
        }
        autumnSchoolOpenDay = systemConfigService.getConfigValue(SystemConfigs.AUTUMN_SCHOOL_OPEN_DAY);
        if (StringUtils.isEmpty(autumnSchoolOpenDay)) {
            throw new InvalidParameterException("请配置秋季开学时间，格式(MM-dd)");
        }
        autumnSchoolCloseDay = systemConfigService.getConfigValue(SystemConfigs.AUTUMN_SCHOOL_CLOSE_DAY);
        if (StringUtils.isEmpty(autumnSchoolCloseDay)) {
            throw new InvalidParameterException("请配置秋季闭学时间，格式(MM-dd)");
        }
        springSchoolOpenDay = systemConfigService.getConfigValue(SystemConfigs.SPRING_SCHOOL_OPEN_DAY);
        if (StringUtils.isEmpty(springSchoolOpenDay)) {
            throw new InvalidParameterException("请配置春季开学时间，格式(MM-dd)");
        }
        springSchoolCloseDay = systemConfigService.getConfigValue(SystemConfigs.SPRING_SCHOOL_CLOSE_DAY);
        if (StringUtils.isEmpty(springSchoolCloseDay)) {
            throw new InvalidParameterException("请配置春季闭学时间，格式(MM-dd)");
        }
    }

    /**
     * @param
     * @return
     * @description 校验是否统计数据
     * @date 2021/8/16 20:21
     */
    public boolean checkOpenSchool(LocalDate localDate) {
        boolean checkFlag = false;
        //当前时间
        LocalDate schoolOpenday = LocalDate.now();
        //判断localDate是否在秋季学期
        if (this.checkLastAutumnSchoolItem(localDate)) {
            checkFlag = true;
            schoolOpenday = getLastAutumnSchoolOpenDay(localDate).getOpenDate();
        }
        //判断是否在春季学期
        if (this.checkLastSpringSchoolItem(localDate)) {
            checkFlag = true;
            schoolOpenday = getLastSpringSchoolOpenDay(localDate).getOpenDate();
        }
        //是否在今年秋季
        if (this.checkAutumnSchoolItem(localDate)) {
            checkFlag = true;
            schoolOpenday = getAutumnSchoolOpenDay(localDate).getOpenDate();
        }
        if (checkFlag) {
            return firstWeekCountBool(localDate, schoolOpenday);
        }
        return false;
    }

    /**
     * @param
     * @return
     * @description 第一周是否统计
     * @date 2021/9/17 16:36
     */
    private boolean firstWeekCountBool(LocalDate localDate, LocalDate schoolOpenDay) {
        LocalDate schoolOpenWeekDate = schoolOpenDay.with(DayOfWeek.MONDAY);
        LocalDate localWeekDate = localDate.with(DayOfWeek.MONDAY);
        if (0 == schoolOpenWeekDate.compareTo(localWeekDate)) {
            if (schoolOpenDay.getDayOfWeek().getValue() > 5) {//开学周如果是周末计入考勤周
                return false;
            }
        }
        return true;
    }


    /**
     * @param
     * @return
     * @description 是否在去年秋季
     * @date 2021/8/19 14:49
     */
    private boolean checkLastAutumnSchoolItem(LocalDate localDate) {
        SchoolDate lastAutumnSchoolOpenDay = this.getLastAutumnSchoolOpenDay(localDate);
        if (localDate.compareTo(lastAutumnSchoolOpenDay.getOpenDate()) >= 0 //是否在去年秋季
                && lastAutumnSchoolOpenDay.getCloseDate().compareTo(localDate) >= 0
        ) {
            return true;
        }
        return false;
    }

    /**
     * @param
     * @return
     * @description 是否在去年春季
     * @date 2021/8/19 14:49
     */
    private boolean checkLastSpringSchoolItem(LocalDate localDate) {
        SchoolDate lastSpringSchoolOpenDay = this.getLastSpringSchoolOpenDay(localDate);
        if (localDate.compareTo(lastSpringSchoolOpenDay.getOpenDate()) >= 0 //是否在去年春季
                && lastSpringSchoolOpenDay.getCloseDate().compareTo(localDate) >= 0
        ) {
            return true;
        }
        return false;
    }

    /**
     * @param
     * @return
     * @description 是否在今年秋季
     * @date 2021/8/19 14:49
     */
    private boolean checkAutumnSchoolItem(LocalDate localDate) {
        SchoolDate autumnSchoolOpenDay = this.getAutumnSchoolOpenDay(localDate);
        if (localDate.compareTo(autumnSchoolOpenDay.getOpenDate()) >= 0
                && autumnSchoolOpenDay.getCloseDate().compareTo(localDate) >= 0
        ) {
            return true;
        }
        return false;
    }

    /**
     * @param
     * @return
     * @description 获取去年秋季开学时间
     * @date 2021/8/19 15:13
     */
    private SchoolDate getLastAutumnSchoolOpenDay(LocalDate localDate) {
        SchoolDate schoolDate = new SchoolDate();
        int year = localDate.getYear();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate schoolOpenDay2 = LocalDate.parse(year - 1 + "-" + this.autumnSchoolOpenDay, dateTimeFormatter);
        LocalDate schoolCloseDay2 = LocalDate.parse(year + "-" + this.autumnSchoolCloseDay, dateTimeFormatter);
        schoolDate.setOpenDate(schoolOpenDay2);
        schoolDate.setCloseDate(schoolCloseDay2);
        return schoolDate;
    }


    /**
     * @param
     * @return
     * @description 获取今年春季开学时间
     * @date 2021/8/19 15:13
     */
    private SchoolDate getLastSpringSchoolOpenDay(LocalDate localDate) {
        SchoolDate schoolDate = new SchoolDate();
        int year = localDate.getYear();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate schoolOpenDay2 = LocalDate.parse(year + "-" + this.springSchoolOpenDay, dateTimeFormatter);
        LocalDate schoolCloseDay2 = LocalDate.parse(year + "-" + this.springSchoolCloseDay, dateTimeFormatter);
        schoolDate.setOpenDate(schoolOpenDay2);
        schoolDate.setCloseDate(schoolCloseDay2);
        return schoolDate;
    }


    /**
     * @param
     * @return
     * @description 获取今年秋季开学时间
     * @date 2021/8/19 15:13
     */
    private SchoolDate getAutumnSchoolOpenDay(LocalDate localDate) {
        SchoolDate schoolDate = new SchoolDate();
        int year = localDate.getYear();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate schoolOpenDay2 = LocalDate.parse(year + "-" + this.autumnSchoolOpenDay, dateTimeFormatter);
        LocalDate schoolCloseDay2 = LocalDate.parse(year + 1 + "-" + this.autumnSchoolCloseDay, dateTimeFormatter);
        schoolDate.setOpenDate(schoolOpenDay2);
        schoolDate.setCloseDate(schoolCloseDay2);
        return schoolDate;
    }


    //localDate 上周日
    public StaticDateBO getStaticDateBO(LocalDate localDate) {
        StaticDateBO staticDateBO = new StaticDateBO();
        LocalDate endDay = localDate.with(DayOfWeek.SUNDAY);
        int year = localDate.getYear();
        //开学时间
        LocalDate schoolOpenDay = LocalDate.now();//初始化
        //学年
        String schoolYear = "-";
        //学期
        String item = "1";
        if (this.checkLastAutumnSchoolItem(localDate)) {//是否去年秋季
            schoolOpenDay = this.getLastAutumnSchoolOpenDay(localDate).getOpenDate();
            schoolYear = year - 1 + "-" + (year);
            item = "1";
        }
        if (this.checkLastSpringSchoolItem(localDate)) {//是否去年春季
            schoolOpenDay = this.getLastSpringSchoolOpenDay(localDate).getOpenDate();
            schoolYear = year - 1 + "-" + (year);
            item = "0";
        }
        if (this.checkAutumnSchoolItem(localDate)) {//今年秋季
            schoolOpenDay = this.getAutumnSchoolOpenDay(localDate).getOpenDate();
            schoolYear = year + "-" + (year + 1);
            item = "1";
        }

        //当前学年,当前学期
        staticDateBO.setSchoolYear(schoolYear);
        staticDateBO.setSchoolTerm(item);

        //上周一
        LocalDate nowWeekDate = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        //schoolOpenDay开学时间,计算此次统计是第几个考勤周期
        staticDateBO.setCheckWeek(getCheckWeek(localDate, schoolOpenDay));
        DateTimeFormatter sf = DateTimeFormatter.ofPattern("yyyy-MM");
        //计算月份
        String yearMonth = sf.format(nowWeekDate);
        staticDateBO.setYearMonth(yearMonth);
        //上周日
        staticDateBO.setEndDate(endDay);
        //开学时间
        staticDateBO.setSchoolOpenDate(schoolOpenDay);
        staticDateBO.setStartDate(nowWeekDate);
        return staticDateBO;
    }


    /**
     * @param
     * @return
     * @description 获取考勤周
     * @date 2021/9/16 10:43
     */
    private Short getCheckWeek(LocalDate localDate, LocalDate schoolOpenDay) {
        //以上周一为起点
        LocalDate nowWeekDate = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long betweenDays;
        short checkWeek;
        //开学时间的第一个周一
        LocalDate schoolOpendWeekFirstDate = schoolOpenDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        //计算 上周一到开始时间的 日期间隔
        betweenDays = ChronoUnit.DAYS.between(schoolOpendWeekFirstDate, nowWeekDate);
        //计算 考勤周期
        checkWeek = (short) (betweenDays / 7);
        //当前日期在开学第一周
        if (schoolOpenDay.getDayOfWeek().getValue() <= 5) {//开学日是周一到周五,则计入考勤周
            ++checkWeek;
        }
        return checkWeek;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initSchoolDay();
    }

    class SchoolDate {
        public LocalDate openDate;
        public LocalDate closeDate;

        public LocalDate getOpenDate() {
            return openDate;
        }

        public void setOpenDate(LocalDate openDate) {
            this.openDate = openDate;
        }

        public LocalDate getCloseDate() {
            return closeDate;
        }

        public void setCloseDate(LocalDate closeDate) {
            this.closeDate = closeDate;
        }
    }


    /**
     * 分区键查询起始日期，避免全表扫描
     *
     * @param staticDateBO
     * @return
     */
    public LocalDate getCreateDateSt(StaticDateBO staticDateBO) {
        String yearMonth = staticDateBO.getYearMonth();
        if (StringUtils.isNotEmpty(yearMonth)) {//只有月份
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(yearMonth + "-01", dateTimeFormatter);
            return localDate;
        }

        return null;
    }

    public LocalDate getCreateDateEd(StaticDateBO staticDateBO) {
        String yearMonth = staticDateBO.getYearMonth();
        if (StringUtils.isNotEmpty(yearMonth)) {//只有月份
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(yearMonth + "-01", dateTimeFormatter);
            LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());//日期最后一天
            //统计规则，当前日期统计之前的数据,本月的最后一个周日再加7天
            LocalDate endDay = lastDayOfMonth.with(DayOfWeek.SUNDAY).plusWeeks(1);
            return endDay;
        }

        return null;
    }

    public WeekDay getSemesterDate(String schoolYear, Integer schoolTerm, Integer checkWeek) {
        String openDayStr;
        if (schoolTerm == 1) {
            openDayStr = schoolYear + "-" + this.autumnSchoolOpenDay;
        } else {
            openDayStr = Integer.valueOf(schoolYear) + 1 + "-" + this.autumnSchoolOpenDay;
        }
        LocalDate weeks = LocalDate.parse(openDayStr).plusWeeks(checkWeek);
        WeekDay weekDay = new WeekDay(weeks.with(DayOfWeek.MONDAY), weeks.with(DayOfWeek.SUNDAY));
        log.info("查询的周期:{},时间范围:{}  ---  {}", checkWeek, weekDay.getMonday(), weekDay.getSunday());
        return weekDay;
    }
}
