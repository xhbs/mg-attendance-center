package com.unisinsight.business.job;

import com.google.common.collect.Lists;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.bo.UserClassMappingBO;
import com.unisinsight.business.manager.SMSManager;
import com.unisinsight.business.mapper.DailyAttendanceWeekResultMapper;
import com.unisinsight.business.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日常考勤 提醒班主任进行有感考勤
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/14
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class DailyAttendanceAlertJob {

    @Resource
    private DailyAttendanceWeekResultMapper dailyAttendanceWeekResultMapper;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private SMSManager smsManager;

    @Value("${sms-tpl.daily-attendance}")
    private String smsTpl;

//    @Scheduled(cron = "${cron.DailyAttendanceAlertJob}")
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("[定时任务] - 发送日常考勤短信提醒 ===>");

        long startedAt = System.currentTimeMillis();
        LocalDate today = LocalDate.now();

        // 查询周考勤结果为缺勤的人员
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        List<PersonOfClassBO> persons = dailyAttendanceWeekResultMapper.findAbsencePersonsAtWeek(monday);
        log.info("查询到在本周 {} 有 {} 个缺勤人员", monday, persons.size());

        if (persons.isEmpty()) {
            return;
        }

        // key: 班级org_index, value: 学生信息
        Map<String, List<PersonOfClassBO>> personClassMap = persons
                .stream()
                .collect(Collectors.groupingBy(PersonOfClassBO::getOrgIndex));
        Set<String> classOrgIndexSet = personClassMap.keySet();

        // 考勤日期
        String attendanceDate = today.format(DateTimeFormatter.ofPattern("MM月dd日"));
        Lists.partition(new ArrayList<>(classOrgIndexSet), 100)// 每次给100个人发
                .forEach(classOrgIndexes -> {
                    List<UserClassMappingBO> mappingUsers = organizationService.getMappingUser(classOrgIndexes);
                    if (mappingUsers.isEmpty()) {
                        log.info("未查询到班级绑定的班主任");
                        return;
                    }

                    for (UserClassMappingBO mappingUser : mappingUsers) {
                        List<PersonOfClassBO> students = personClassMap.get(mappingUser.getOrgIndex());
                        sendSMS(mappingUser, attendanceDate, students);
                    }
                });

        log.info("[定时任务] - 发送日常考勤短信提醒，耗时：{}ms <===", System.currentTimeMillis() - startedAt);
    }

    /**
     * 给班主任发送短信
     */
    private void sendSMS(UserClassMappingBO user, String attendanceDate, List<PersonOfClassBO> students) {
        if (user.getCellPhone() == null) {
            log.warn("班主任 {} 未设置手机号码，无法发送短信", user.getUserName());
            return;
        }
        String personNames = students
                .stream()
                .map(PersonOfClassBO::getPersonName)
                .collect(Collectors.joining("、"));

        Map<String, String> params = new HashMap<>(8);
        params.put("user_name", user.getUserName());
        params.put("date", attendanceDate);
        params.put("students", personNames);
        smsManager.sendAsync(user.getCellPhone(), smsTpl, params);
    }
}
