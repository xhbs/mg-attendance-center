package com.unisinsight.business.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.unisinsight.business.dto.ObjectManageDataSourceConf;
import com.unisinsight.business.dto.PersonDTO;
import com.unisinsight.business.mapper.AdsStudentBasicInformationMapper;
import com.unisinsight.business.mapper.PersonMapper;
import com.unisinsight.business.model.AdsStudentBasicInformation;
import com.unisinsight.business.service.impl.StudentImageService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class StudentImageJob {

    @Value("${dbServer.ip}")
    private String dbServerIp;

    @Value("${dbServer.port}")
    private String dbServerPort;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StudentImageService studentImageService;

    @Resource
    private AdsStudentBasicInformationMapper adsStudentBasicInformationMapper;
    @Resource
    private PersonMapper personMapper;
    @Resource
    private JdbcTemplate objectManageJdbcTemplate;
    @Resource
    private StuAndOrgSyncJob stuAndOrgSyncJob;
    @Resource
    private Executor workExecotor;
    @Resource
    private ObjectManageDataSourceConf objectManageDataSourceConf;

    //    @Scheduled(cron = "0 0 2 * * ?")
    public void syncImageFromFtp() throws Exception {
        studentImageService.importStudentImage();
    }

    //    @Scheduled(cron = "0 0 6 1/2 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncImageFromDatabase() throws JSchException, SftpException {
        studentImageService.downloadImageInDatabase();
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0/2 * * * ?")
    public void syncImageFronObjectManage() throws InterruptedException {
        //从object-manage同步图片
        log.info("从object-manage同步图片到adsStudentBasicInformation,person");
        RLock lock = redissonClient.getLock("adsStudentBasicInformation-sync");
        if (lock.tryLock(60, 60, TimeUnit.SECONDS)) {
            try {
                adsStudentBasicInformationMapper.sync(dbServerIp, dbServerPort, dbUserName, dbPassword);
                personMapper.syncImage(dbServerIp, dbServerPort, dbUserName, dbPassword);
                log.info("从object-manage同步图片到adsStudentBasicInformation,person成功");
            } finally {
                lock.unlock();
            }
        } else {
            log.info("从object-manage同步图片到adsStudentBasicInformation,person失败,另一个程序正在同步数据");
        }

    }

    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncImageFromAds() throws JSchException, InterruptedException, SQLException {
        log.info("syncImageFromAds--开始执行");
        studentImageService.initSession();
        log.info("syncImageFromAds--初始化Session成功");

        int pageSize = 1000; // 每页记录数
        int offset = 0;
        int limit = pageSize;

//        String selectXszpCountFromAdsSql = "select count(1) as count from public.ads_student_basic_information ads left join public.tb_person p on p.identity_no = ads.sfzjh where ads.xszp is not null and ads.xszp !='' and p.user_image is null and identity_no not in (select id_number from ft.mdk_error group by id_number)";
        String selectXszpCountFromAdsSql = "select count(1) as count from public.ads_student_basic_information ads left join public.tb_person p on p.identity_no = ads.sfzjh where ads.xszp is not null and ads.xszp !='' and p.user_image is null";
//        String selectXszpFromAdsSql = "select * from public.ads_student_basic_information ads left join public.tb_person p on p.identity_no = ads.sfzjh where ads.xszp is not null and ads.xszp !='' and p.user_image is null and identity_no not in (select id_number from ft.mdk_error group by id_number) LIMIT ? OFFSET ?";
        String selectXszpFromAdsSql = "select * from public.ads_student_basic_information ads left join public.tb_person p on p.identity_no = ads.sfzjh where ads.xszp is not null and ads.xszp !='' and p.user_image is null LIMIT ? OFFSET ?";

        Map<String, Object> countMap = objectManageJdbcTemplate.queryForMap(selectXszpCountFromAdsSql);
        long count = (long) countMap.get("count");
        log.info("syncImageFromAds--未注册学生数量：{}", count);
        if (count == 0) {
            return;
        }
        AtomicInteger integer = new AtomicInteger(1);
        AtomicInteger errorImageCount = new AtomicInteger(0);
        while (true) {
            long pageTime = System.currentTimeMillis();
            AtomicInteger count1 = new AtomicInteger(1);
            //分页查询未进行注册 并且学籍系统中有图片的学生照片
            reinitializeDataSource();
            List<AdsStudentBasicInformation> adsStudentBasicInformations = objectManageJdbcTemplate.query(selectXszpFromAdsSql, new Object[]{limit, offset}, BeanPropertyRowMapper.newInstance(AdsStudentBasicInformation.class));
            log.info("syncImageFromAds--total：{}，查询数量：{}", count, adsStudentBasicInformations.size());
            if(CollUtil.isEmpty(adsStudentBasicInformations)){
                break;
            }
            //拆分list，用于多线程下载图片,拆分成50个list，每个线程只执行20条。
            List<List<AdsStudentBasicInformation>> splitAvg = ListUtil.splitAvg(adsStudentBasicInformations, 50);
            CountDownLatch latch = new CountDownLatch(splitAvg.size());
            for (List<AdsStudentBasicInformation> studentBasicInformations : splitAvg) {
                workExecotor.execute(() -> {
                    try {
                        //splitAvg可能有空数组
                        if (studentBasicInformations.isEmpty()) {
                            return;
                        }
                        for (AdsStudentBasicInformation basicInformation : studentBasicInformations) {
                            log.info("syncImageFromAds--第{}个，总数：{}，当前执行总数：{},errorImageCount:{}", count1.getAndIncrement(), count, integer.getAndIncrement(),errorImageCount.get());
                            //下载图片,会修改图片到tb_person中
                            long time = System.currentTimeMillis();
                            String image = studentImageService.downloadAndRegister(basicInformation);
                            if (StrUtil.isBlank(image)) {
                                errorImageCount.incrementAndGet();
                            }
                            long l = System.currentTimeMillis() - time;
                            log.info("syncImageFromAds--执行下载图片任务耗时：{}s", (double) l / 1000);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            log.info("syncImageFromAds--执行一组数据耗时：{}s", (double) (System.currentTimeMillis() - pageTime) / 1000);
            offset = errorImageCount.get();
        }
    }

    private void reinitializeDataSource() {
        // 关闭原有数据源连接池
        HikariDataSource dataSource = (HikariDataSource) objectManageJdbcTemplate.getDataSource();
        dataSource.close();

        HikariDataSource dataSourceUuv = new HikariDataSource();
        dataSourceUuv.setJdbcUrl(objectManageDataSourceConf.getJdbcUrl());
        dataSourceUuv.setDriverClassName(objectManageDataSourceConf.getDriverClassName());
        dataSourceUuv.setUsername(objectManageDataSourceConf.getUsername());
        dataSourceUuv.setPassword(objectManageDataSourceConf.getPassword());
        // 设置新的数据源到JdbcTemplate
        objectManageJdbcTemplate.setDataSource(dataSourceUuv);
    }
}
