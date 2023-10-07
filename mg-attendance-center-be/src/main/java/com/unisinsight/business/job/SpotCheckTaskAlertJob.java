package com.unisinsight.business.job;

import com.google.common.collect.Lists;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.bo.UserClassMappingBO;
import com.unisinsight.business.manager.SMSManager;
import com.unisinsight.business.mapper.TaskPersonRelationMapper;
import com.unisinsight.business.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽查考勤给老师发短信提醒定时任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/6
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class SpotCheckTaskAlertJob {

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private SMSManager smsManager;

    @Value("${sms-tpl.spot-check-task}")
    private String smsTpl;

    @Scheduled(cron = "${cron.SpotCheckTaskAlertJob}")
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("[定时任务] - 发送抽查考勤短信提醒 ===>");
        long startedAt = System.currentTimeMillis();

        // 查询出明天是考勤日期的任务
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();
        List<PersonOfClassBO> persons = taskPersonRelationMapper.findPersonsAtDate(today);
        log.info("查询在 {} 有 {} 个人员有抽查任务", today, persons.size());

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

        log.info("[定时任务] - 发送抽查考勤短信提醒完成，耗时：{}ms <===", System.currentTimeMillis() - startedAt);
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
