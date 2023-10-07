package com.unisinsight.business.job;

import com.unisinsight.business.mapper.PersonMapper;
import com.unisinsight.business.mapper.TaskPersonRelationMapper;
import com.unisinsight.business.service.AttendanceEventFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 考勤人员同步任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/16
 */
@Component
@DependsOn("flywayInitializer")
@Slf4j
public class PersonSyncJob implements ApplicationRunner, Runnable {

    @Value("${dbServer.ip}")
    private String dbServerIp;

    @Value("${dbServer.port}")
    private String dbServerPort;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Resource
    private PersonMapper personMapper;

    @Resource
    private AttendanceEventFilter attendanceEventFilter;

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Scheduled(cron = "${cron.PersonSyncJob}")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run() {
//        log.info("[定时任务] - 同步考勤人员开始 ===>");

        long startedAt = System.currentTimeMillis();
//        personMapper.sync(dbServerIp, dbServerPort, dbUserName, dbPassword); // 同步65w人员 耗时 23.6s
//        log.info("[定时任务] - 同步考勤人员完成，耗时：{}ms <===", System.currentTimeMillis() - startedAt);

        startedAt = System.currentTimeMillis();
        attendanceEventFilter.refreshPersonCache();
        log.info("[定时任务] - 刷新人员缓存完成，耗时：{}ms <===", System.currentTimeMillis() - startedAt);

        startedAt = System.currentTimeMillis();
        taskPersonRelationMapper.deleteNotAtSchoolPersons();
        log.info("[定时任务] - 删除已经不在校的抽查人员，耗时：{}ms <===", System.currentTimeMillis() - startedAt);
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(this).start();
    }
}
