package com.unisinsight.business.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.jcraft.jsch.JSchException;
import com.unisinsight.business.dto.response.SchoolInfo;
import com.unisinsight.business.mapper.AdsStudentBasicInformationMapper;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.mapper.PersonMapper;
import com.unisinsight.business.model.AdsStudentBasicInformation;
import com.unisinsight.business.model.PersonDO;
import com.unisinsight.business.service.impl.StudentImageService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 学生和组织同步任务
 * by XieHaiBo
 */

@Component
@Slf4j
public class StuAndOrgSyncJob {

    @Resource
    private AdsStudentBasicInformationMapper adsStudentBasicInformationMapper;
    @Resource
    private JdbcTemplate ziguangMysqlJdbcTemplate;
    @Resource
    private JdbcTemplate objectManageJdbcTemplate;
    @Resource
    private PersonMapper personMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private StudentImageService studentImageService;
    @Resource
    private RedissonClient redissonClient;

    private final String ECO_URL = "/api/eco/v1/dcg/middle-office/increment/synYnMiddleOfficeInfo";

    @Value("${service.eco-dcg.base-url}")
    private String ecoHost;

    /**
     * 先执行eco同步,每天执行一次增量同步
     */
//    @Scheduled(cron = "0 10 0 * * ?")
    public void syncEco() {
        log.info("执行eco同步逻辑");
        String url = ecoHost + ECO_URL;
        String body = HttpUtil.createPost(url).execute().body();
    }

    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0 0 0/1 * * ?")
    public void run() throws JSchException, InterruptedException {
        run(null,null);
    }

    /**
     * 每小时同步一次 从上次同步时间开始抽取数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void run(String date,Integer timeOut) throws JSchException, InterruptedException {
        RLock lock = redissonClient.getLock("adsStudentBasicInformation-sync");
        if (lock.tryLock(120, timeOut == null ? 600 : timeOut, TimeUnit.SECONDS)) {
            try {
                if(StrUtil.isNotBlank(date)){
                    sync(date);
                }else{
                    String day;
                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String s = stringRedisTemplate.opsForValue().get("sync-org-student");
                    if (StrUtil.isNotBlank(s)) {
                        day = s;
                    } else {
                        day = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, -2).toString("yyyy-MM-dd HH:mm:ss");
                    }
                    sync(day);
                    stringRedisTemplate.opsForValue().set("sync-org-student", now);
                }
            } finally {
                lock.unlock();
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void sync(String date) throws JSchException {
        syncOrgState(date);
        syncStu(date);
    }

    /**
     * 从学籍底表同步数据到 pg库
     */
    private void syncStu(String date) {
        String sqlModel = ResourceUtil.readStr("file/studentBasicInformation.txt", Charset.defaultCharset());
        int startPage = 0;
        int pageSize = 1000;
        log.info("sync-student||开始同步学生,查询{}之后修改的数据", date);
        Integer sum = 0;
        while (true) {
            String sql = sqlModel.replace("#{updateTime}", date).replace("#{startPage}", String.valueOf(startPage)).replace("#{pageSize}", String.valueOf(pageSize));
            List<AdsStudentBasicInformation> list = ziguangMysqlJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AdsStudentBasicInformation.class));
            list = list.stream().filter(v -> StrUtil.isNotBlank(v.getSfzjh()) && StrUtil.isNotBlank(v.getOrgIndex()) && StrUtil.isNotBlank(v.getXxIndex())).collect(Collectors.toList());
            if (CollUtil.isEmpty(list)) {
                break;
            }
            try {
                Integer count = sync(list);
                sum += count;
            } catch (Exception e) {
                log.info("一组数据同步失败", e);
            }
            startPage += pageSize;
        }
        log.info("sync-student||同步完成,共{}条数据", sum);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer sync(List<AdsStudentBasicInformation> list) throws JSchException {
        int sum = 0;
        Example build = Example.builder(AdsStudentBasicInformation.class)
                .where(Sqls.custom()
                        .andIn("sfzjh", list.stream().map(AdsStudentBasicInformation::getSfzjh).collect(Collectors.toList()))
                        .andIsNotNull("image")
                        .andNotEqualTo("image", "")
                ).build();
        //已经注册过的
        List<AdsStudentBasicInformation> informations = adsStudentBasicInformationMapper.selectByCondition(build);
        sum += list.size();
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        log.info("sync-student||查询到{}条学生数据,开始同步", list.size());
        for (AdsStudentBasicInformation information : list) {
            if (StrUtil.isBlank(information.getXszp())) {
                continue;
            }
            Optional<AdsStudentBasicInformation> optional = informations.stream().filter(v -> v.getSfzjh().equals(information.getSfzjh())).findFirst();
            if (optional.isPresent()) {
                //图片相同 不重新下载图片
                if (information.getXszp().equals(optional.get().getXszp())) continue;
            }
            studentImageService.initSession();
            String image = studentImageService.downloadAndRegister(information);
            if (StrUtil.isNotBlank(image)) information.setImage(image);
        }
        adsStudentBasicInformationMapper.insertBatch(list);
        log.info("sync-student||同步数据到adsStudentBasicInformation成功");
        log.info("sync-student||开始同步数据到Persons表");
        List<PersonDO> persons = generatePersons(list);
        personMapper.batchSaveOrUpdateNew(persons);
        log.info("sync-student||同步数据到persons成功");
        updateTbPerson(list);
        return sum;
    }

    private void updateTbPerson(List<AdsStudentBasicInformation> list) {

        List<AdsStudentBasicInformation> zx = list.stream().filter(v -> v.getZxszt().equals("00") && v.getZt().equals("A")).collect(Collectors.toList());
        List<AdsStudentBasicInformation> bzx = list.stream().filter(v -> !v.getZxszt().equals("00") && v.getZt().equals("A")).collect(Collectors.toList());
        List<AdsStudentBasicInformation> czx = list.stream().filter(v -> !v.getZxszt().equals("00") && !v.getZt().equals("A")).collect(Collectors.toList());
        String sql1 = "update public.tb_person set is_record = 0,is_school = 0 where identity_no in ('" + zx.stream().map(AdsStudentBasicInformation::getSfzjh).collect(Collectors.joining("','")) + "')";
        String sql2 = "update public.tb_person set is_record = 1,is_school = 0 where identity_no in ('" + bzx.stream().map(AdsStudentBasicInformation::getSfzjh).collect(Collectors.joining("','")) + "')";
        String sql3 = "update public.tb_person set is_record = 1,is_school = 1 where identity_no in ('" + czx.stream().map(AdsStudentBasicInformation::getSfzjh).collect(Collectors.joining("','")) + "')";

        objectManageJdbcTemplate.update(sql1);
        objectManageJdbcTemplate.update(sql2);
        objectManageJdbcTemplate.update(sql3);

    }

    private List<PersonDO> generatePersons(List<AdsStudentBasicInformation> list) {
        return list.stream().filter(v -> StrUtil.isNotBlank(v.getOrgIndex())).map(v -> {
            PersonDO personDO = new PersonDO();
            personDO.setPersonNo(v.getSfzjh());
            personDO.setPersonName(v.getXm());
            personDO.setPersonUrl(v.getImage());
            personDO.setOrgIndex(v.getOrgIndex());
            personDO.setOrgName(v.getBjmc());
            personDO.setAtSchool(v.getZxszt().equalsIgnoreCase("00"));
            personDO.setRegistered(v.getZt().equalsIgnoreCase("A"));
            personDO.setGender(String.valueOf(v.getXb().equalsIgnoreCase("男") ? 1 : 2));
            return personDO;
        }).collect(Collectors.toList());
    }


    /**
     * 只同步组织状态,组织数据还是由eco进行同步
     *
     * @param date
     */
    public void syncOrgState(String date) {
        log.info("sync-org||同步学校状态");
        String sql = "select * from zz_xx where UPDATETIME__ >= '" + date + "'";
        List<SchoolInfo> schoolInfos = ziguangMysqlJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SchoolInfo.class));
        log.info("sync-org||查询到{}日之后修改的{}个学校", date, schoolInfos.size());
        //资助和学籍状态都为A的学校
        List<SchoolInfo> aSchools = schoolInfos.stream().filter(v -> v.getZt().equals("A") && v.getXjzt().equals("A")).collect(Collectors.toList());
        List<SchoolInfo> pSchools = schoolInfos.stream().filter(v -> v.getZt().equals("P") && v.getXjzt().equals("P")).collect(Collectors.toList());
        log.info("sync-org||正常状态学校数量:{}", aSchools.size());
        log.info("sync-org||非正常状态学校数量:{}", pSchools.size());
        if (CollUtil.isNotEmpty(pSchools)) {
            organizationMapper.stateBatchUpdate(pSchools.stream().map(SchoolInfo::getId).collect(Collectors.toList()), "P");
        }
        if (CollUtil.isNotEmpty(aSchools)) {
            organizationMapper.stateBatchUpdate(aSchools.stream().map(SchoolInfo::getId).collect(Collectors.toList()), "A");
        }
    }

}
