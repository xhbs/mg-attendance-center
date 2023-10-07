package com.unisinsight.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.unisinsight.business.client.uuv.UuvClient;
import com.unisinsight.business.client.uuv.res.RoleDetailRes;
import com.unisinsight.business.client.uuv.res.UserDetailRes;
import com.unisinsight.business.common.utils.TransferDataUtils;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.*;
import com.unisinsight.business.dto.request.StatisticsCityReq;
import com.unisinsight.business.dto.request.StatisticsProvinceReq;
import com.unisinsight.business.dto.request.StatisticsReq;
import com.unisinsight.business.dto.request.StatisticsTermReq;
import com.unisinsight.business.mapper.DailyAttendanceWeekResultMapper;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.service.StatisticsService;
import com.unisinsight.framework.common.exception.BaseErrorCode;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName : mg-attendance-center
 * @Description : 统计
 * @Author : xiehb
 * @Date: 2022/11/04 09:07
 * @Version 1.0.0
 */
@Service
@Slf4j
@CacheConfig(keyGenerator = "statisticsKeyGenerator")
public class StatisticsServiceImpl implements StatisticsService {

    //省直属学校id
    @Value("${statistics.id.province}")
    private String provinceId;

    @Resource
    private DailyAttendanceWeekResultMapper dailyAttendanceWeekResultMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private TransferDataUtils transferDataUtils;
    @Resource
    private Executor statisticsExecutor;
    @Resource
    private UuvClient uuvClient;

    private static final String CITY = "city";
    public static final String CITY_STR = "市";
    public static final String SCHOOL_STR = "校";
    public static final String PROVINCE_STR = "省";


    private List<String> provinceIdList = new ArrayList<>(60);

    private final Map<String, List<String>> citySchoolMap = new HashMap<>(20);
    private final Map<String, String> orgIndex2NameMap = new HashMap<>(20);

    /**
     * 省直属/各地州 考勤统计
     *
     * @param req
     * @return
     */
    @Override
    @Cacheable(value = "provinceStatistics")
    public StatisticsProvince provinceStatistics(StatisticsProvinceReq req) {
        auth(PROVINCE_STR);
        return privateProvinceStatistics(req);
    }

    private StatisticsProvince privateProvinceStatistics(StatisticsProvinceReq req) {
        log.info("统计省直属/各地州学校在校情况.学年:{},学期:{},周期:{}", req.getSchoolYear(), req.getSchoolTerm() == 1 ? "秋季" : "春季", req.getCheckWeek());
        if (CollUtil.isEmpty(provinceIdList)) {
            List<OrganizationDO> childrens = organizationMapper.getChildren(provinceId, null);
            provinceIdList = childrens.stream().map(OrganizationDO::getOrgIndex).collect(Collectors.toList());
        }
        WeekDay weekDay = transferDataUtils.getSemesterDate(req.getSchoolYear(), req.getSchoolTerm(), req.getCheckWeek());
        StatisticsProvince statisticsProvince;
        try {
            if (CITY.equals(req.getType())) {
                statisticsProvince = dailyAttendanceWeekResultMapper.statisticsNotInParents(concatTableSuffix(weekDay.getMonday(), weekDay.getSunday()), provinceIdList);
            } else {
                statisticsProvince = dailyAttendanceWeekResultMapper.statisticsByParents(concatTableSuffix(weekDay.getMonday(), weekDay.getSunday()), provinceIdList);
            }
        } catch (Exception e) {
            statisticsProvince = null;
            log.info(e.getMessage());
        }
        if (ObjectUtil.isEmpty(statisticsProvince)) {
            return new StatisticsProvince();
        }
        calc(statisticsProvince);
        return statisticsProvince;
    }

    /**
     * 校级考勤统计
     *
     * @param req
     * @return
     */
    @Override
    @Cacheable(value = "schoolStatistics")
    public StatisticsProvince schoolStatistics(StatisticsReq req) {
        UserDetail user = auth(SCHOOL_STR);
        StatisticsCityReq cityReq = BeanUtil.copyProperties(req, StatisticsCityReq.class);
        cityReq.setOrgIndex(user.getOrgIndex());
        return privateSchoolStatistics(cityReq);
    }

    private StatisticsProvince privateSchoolStatistics(StatisticsCityReq req) {
        WeekDay weekDay = transferDataUtils.getSemesterDate(req.getSchoolYear(), req.getSchoolTerm(), req.getCheckWeek());

        String schoolId = req.getOrgIndex();
        StatisticsProvince school = new StatisticsProvince();
        try {
            school = dailyAttendanceWeekResultMapper.statisticsByParents(concatTableSuffix(weekDay.getMonday(), weekDay.getSunday()), Collections.singletonList(schoolId));
            if (ObjectUtil.isNotEmpty(school)) {
                calc(school);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return ObjectUtil.defaultIfNull(school, new StatisticsProvince());
    }

    /**
     * 市级考勤统计
     *
     * @param req
     * @return
     */
    @Override
    @Cacheable(value = "cityStatistics")
    public StatisticsProvince cityStatistics(StatisticsReq req) {
        StatisticsCityReq statisticsCityReq = BeanUtil.copyProperties(req, StatisticsCityReq.class);
        UserDetail user = auth(CITY_STR);
        statisticsCityReq.setOrgIndex(user.getOrgIndex());
        return privateCityStatistics(statisticsCityReq);
    }

    private StatisticsProvince privateCityStatistics(StatisticsCityReq req) {
        WeekDay weekDay = transferDataUtils.getSemesterDate(req.getSchoolYear(), req.getSchoolTerm(), req.getCheckWeek());
        String cityId = req.getOrgIndex();
        //用户的所属州市id
        if (!citySchoolMap.containsKey(cityId)) {
            List<String> schools = new ArrayList<>();
            //州市下的学校列表
            getOrgChildren(CollUtil.newArrayList(cityId), schools);
            //加入缓存
            citySchoolMap.put(cityId, schools);
        }
        List<String> schoolIds = citySchoolMap.getOrDefault(cityId, CollUtil.newArrayList());
        StatisticsProvince statistics = new StatisticsProvince();
        try {
            statistics = dailyAttendanceWeekResultMapper.statisticsByParents(concatTableSuffix(weekDay.getMonday(), weekDay.getSunday()), schoolIds);
            if (ObjectUtil.isNotEmpty(statistics)) {
                calc(statistics);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return ObjectUtil.defaultIfNull(statistics, new StatisticsProvince());
    }

    private void calc(StatisticsProvince data) {
        if (ObjectUtil.isEmpty(data)) {
            return;
        }
        data.setCount(data.getAbsentWeeks() + data.getNormalWeeks());
        data.setAbsentWeeksPercent(BigDecimal.valueOf(data.getAbsentWeeks()).divide(BigDecimal.valueOf(data.getCount()), 4, RoundingMode.HALF_UP));
        data.setNormalWeeksPercent(BigDecimal.valueOf(data.getNormalWeeks()).divide(BigDecimal.valueOf(data.getCount()), 4, RoundingMode.HALF_UP));
    }

    /**
     * 省内学校考勤top5
     *
     * @param req
     * @return
     */
    @Override
    @Cacheable(value = "allSchoolStatistics")
    public List<StatisticsAllSchool> allSchoolStatistics(StatisticsReq req) {
        auth(PROVINCE_STR);
        log.info("统计省内所有学校考勤率前五名.学年:{},学期:{},周期:{}", req.getSchoolYear(), req.getSchoolTerm() == 1 ? "秋季" : "春季", req.getCheckWeek());
        WeekDay weekDay = transferDataUtils.getSemesterDate(req.getSchoolYear(), req.getSchoolTerm(), req.getCheckWeek());
        List<StatisticsAllSchool> allSchools;
        try {
            allSchools = dailyAttendanceWeekResultMapper.allSchoolStatistics(concatTableSuffix(weekDay.getMonday(), weekDay.getSunday()));
            calc(allSchools);
        } catch (Exception e) {
            allSchools = Collections.emptyList();
            log.info(e.getMessage());
        }
        return allSchools;
    }

    /**
     * 市级学校考勤top5
     *
     * @param req
     * @return
     */
    @Override
    @Cacheable(value = "citySchoolTop5Statistics")
    public List<StatisticsAllSchool> citySchoolTop5Statistics(StatisticsReq req) {
        UserDetail user = auth(CITY_STR);
        log.info("统计-{}-所有学校考勤率前五名.学年:{},学期:{},周期:{}", user.getOrgName(), req.getSchoolYear(), req.getSchoolTerm() == 1 ? "秋季" : "春季", req.getCheckWeek());
        String cityId = user.getOrgIndex();
        WeekDay weekDay = transferDataUtils.getSemesterDate(req.getSchoolYear(), req.getSchoolTerm(), req.getCheckWeek());
        List<StatisticsAllSchool> allSchools;

        try {
            List<String> schools = new ArrayList<>();
            getOrgChildren(CollUtil.newArrayList(cityId), schools);
            allSchools = dailyAttendanceWeekResultMapper.statisticsBySchoolIds(concatTableSuffix(weekDay.getMonday(), weekDay.getSunday()), schools);
            calc(allSchools);
        } catch (Exception e) {
            allSchools = Collections.emptyList();
            log.info(e.getMessage());
        }
        return allSchools;
    }

    private UserDetail auth(String name) {
        UserDetail user = getUser();
        if (CollUtil.isEmpty(user.getRoles()) || user.getRoles().stream().noneMatch(v -> v.getRoleName().contains(name))) {
            throw BaseException.of(BaseErrorCode.PERMISSION_DENY_ERROR);
        }
        return user;
    }

    private void calc(List<StatisticsAllSchool> allSchools) {
        allSchools.forEach(v -> {
            v.setCount(v.getAbsentWeeks() + v.getNormalWeeks());
            v.setAbsentWeeksPercent(BigDecimal.valueOf(v.getAbsentWeeks()).divide(BigDecimal.valueOf(v.getCount()), 4, RoundingMode.HALF_UP));
            v.setNormalWeeksPercent(BigDecimal.valueOf(v.getNormalWeeks()).divide(BigDecimal.valueOf(v.getCount()), 4, RoundingMode.HALF_UP));
        });
    }

    /**
     * 省内学校数量
     *
     * @return
     */
    @Override
    @Cacheable(value = "schoolNumberStatistics")
    public SchoolNumStatisticsRes schoolNumberStatistics() {
        auth(PROVINCE_STR);
        Integer schoolCount = organizationMapper.allSchoolCount(4);
        Integer provinceSchoolCount = organizationMapper.provinceSchoolCount(provinceId);
        SchoolNumStatisticsRes res = new SchoolNumStatisticsRes();
        res.setCitySchoolCount(schoolCount - provinceSchoolCount);
        res.setProvinceSchoolCount(provinceSchoolCount);
        res.setCount(schoolCount);
        res.setCitySchoolCountPercent(BigDecimal.valueOf(res.getCitySchoolCount()).divide(BigDecimal.valueOf(schoolCount), 4, RoundingMode.HALF_UP));
        res.setProvinceSchoolPercent(BigDecimal.valueOf(res.getProvinceSchoolCount()).divide(BigDecimal.valueOf(schoolCount), 4, RoundingMode.HALF_UP));
        return res;
    }

    /**
     * 省直属学校学期考勤情况
     *
     * @param req
     * @return
     * @throws InterruptedException
     */
    @Override
    @Cacheable(value = "statisticsTerm")
    public List<TermStatisticsProvince> statisticsTerm(StatisticsReq req) throws InterruptedException {
        auth(PROVINCE_STR);
        StatisticsTermReq bean = BeanUtil.copyProperties(req, StatisticsTermReq.class);
        bean.setType(PROVINCE_STR);
        return getStatisticsTerm(bean);
    }

    /**
     * 州市学校学期考勤情况
     *
     * @param req
     * @return
     * @throws InterruptedException
     */
    @Cacheable(value = "cityStatisticsTerm")
    @Override
    public List<TermStatisticsProvince> cityStatisticsTerm(StatisticsReq req) throws InterruptedException {
        UserDetail user = auth(CITY_STR);
        StatisticsTermReq bean = BeanUtil.copyProperties(req, StatisticsTermReq.class);
        bean.setOrgIndex(user.getOrgIndex());
        bean.setType(CITY_STR);
        return getStatisticsTerm(bean);
    }


    /**
     * 学校学期考勤情况
     *
     * @param req
     * @return
     * @throws InterruptedException
     */
    @Cacheable(value = "schoolStatisticsTerm")
    @Override
    public List<TermStatisticsProvince> schoolStatisticsTerm(StatisticsReq req) throws InterruptedException {
        UserDetail user = auth(SCHOOL_STR);
        StatisticsTermReq bean = BeanUtil.copyProperties(req, StatisticsTermReq.class);
        bean.setOrgIndex(user.getOrgIndex());
        bean.setType(SCHOOL_STR);
        return getStatisticsTerm(bean);
    }

    private List<TermStatisticsProvince> getStatisticsTerm(StatisticsTermReq req) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(20);
        List<TermStatisticsProvince> list = new Vector<>();
        AtomicInteger integer = new AtomicInteger(0);
        for (int i = 0; i < 20; i++) {
            statisticsExecutor.execute(() -> {
                int checkWeek = integer.getAndIncrement();
                StatisticsCityReq cityReq = BeanUtil.copyProperties(req, StatisticsCityReq.class);
                try {
                    cityReq.setOrgIndex(req.getOrgIndex());
                    cityReq.setCheckWeek(checkWeek);
                    StatisticsProvince statisticsProvince = new StatisticsProvince();
                    switch (req.getType()) {
                        case SCHOOL_STR:
                            statisticsProvince = this.privateSchoolStatistics(cityReq);
                            break;
                        case CITY_STR:
                            statisticsProvince = this.privateCityStatistics(cityReq);
                            break;
                        default:
                            statisticsProvince = this.privateProvinceStatistics(BeanUtil.copyProperties(cityReq, StatisticsProvinceReq.class));
                            break;
                    }
                    list.add(new TermStatisticsProvince(checkWeek, ObjectUtil.isEmpty(statisticsProvince) ? new StatisticsProvince() : statisticsProvince));
                } catch (Exception e) {
                    log.info(e.getMessage());
                    list.add(new TermStatisticsProvince(checkWeek, new StatisticsProvince()));
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        return list;
    }

    @Cacheable(value = "classTop5")
    @Override
    public List<StatisticsAllSchool> classTop5(StatisticsReq req) {
        UserDetail user = auth(SCHOOL_STR);
        String schoolId = user.getOrgIndex();
        WeekDay weekDay = transferDataUtils.getSemesterDate(req.getSchoolYear(), req.getSchoolTerm(), req.getCheckWeek());
        List<StatisticsAllSchool> schools = new ArrayList<>();
        try {
            schools = dailyAttendanceWeekResultMapper.classStatistics(concatTableSuffix(weekDay.getMonday(), weekDay.getSunday()), schoolId);
            calc(schools);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return CollUtil.isEmpty(schools) ? new ArrayList<>() : schools;
    }

    /**
     * 统计 省下面各个城市的考勤
     *
     * @param req
     */
    @Cacheable(value = "statisticsProvince2City")
    @Override
    public List<StatisticsEvery> statisticsProvince2City(StatisticsReq req) throws InterruptedException {
        auth(PROVINCE_STR);
        if (citySchoolMap.isEmpty()) {
            initCity2School();
        }
        CountDownLatch latch = new CountDownLatch(citySchoolMap.size());
        Vector<StatisticsEvery> list = new Vector<>();
        for (String city : citySchoolMap.keySet()) {
            statisticsExecutor.execute(() -> {
                try {
                    StatisticsCityReq cityReq = BeanUtil.copyProperties(req, StatisticsCityReq.class);
                    cityReq.setOrgIndex(city);
                    StatisticsProvince statistic = this.privateCityStatistics(cityReq);
                    StatisticsEvery every = BeanUtil.copyProperties(statistic, StatisticsEvery.class);
                    every.setName(orgIndex2NameMap.get(city));
                    every.setOrgIndex(city);
                    list.add(every);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        list.sort(Comparator.comparing(StatisticsEvery::getOrgIndex));
        return list;
    }

    /**
     * 统计 各个城市下的学校的考勤
     *
     * @param req
     */
    @Cacheable(value = "statisticsCity2School")
    @Override
    public List<StatisticsEvery> statisticsCity2School(StatisticsReq req) throws InterruptedException {
        UserDetail user = auth(CITY_STR);
        if (citySchoolMap.isEmpty()) {
            initCity2School();
        }
        String city = user.getOrgIndex();
        CountDownLatch latch = new CountDownLatch(citySchoolMap.size());
        Vector<StatisticsEvery> list = new Vector<>();
        for (String schoolId : citySchoolMap.get(city)) {
            statisticsExecutor.execute(() -> {
                try {
                    StatisticsCityReq cityReq = BeanUtil.copyProperties(req, StatisticsCityReq.class);
                    cityReq.setOrgIndex(schoolId);
                    StatisticsProvince statistic = this.privateSchoolStatistics(cityReq);
                    StatisticsEvery every = BeanUtil.copyProperties(statistic, StatisticsEvery.class);
                    every.setName(orgIndex2NameMap.get(schoolId));
                    every.setOrgIndex(schoolId);
                    list.add(every);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        list.sort(Comparator.comparing(StatisticsEvery::getOrgIndex));
        return list;
    }

    private String concatTableSuffix(LocalDate beginDay, LocalDate endDay) {
        return "_" + beginDay.toString().replaceAll("-", "") + "_" + endDay.toString().replaceAll("-", "");
    }

    @Override
    public UserDetail getUser() {
        User user = UserUtils.getUser();
        UserDetailRes userDetail;
        //获取用户详情信息
        try {
            userDetail = uuvClient.userDetail(user.getUserCode());
        } catch (Exception e) {
            throw BaseException.of(BaseErrorCode.USER_NOT_EXIST);
        }

        //封装对象
        UserDetail ud = new UserDetail();
        ud.setUserCode(userDetail.getUserCode());
        ud.setUserName(userDetail.getUserName());
        ud.setOrgName(userDetail.getOrgName());
        ud.setOrgIndex(userDetail.getOrgIndex());

        //没有任何角色
        if (CollUtil.isEmpty(userDetail.getRoles())) {
            throw BaseException.of(BaseErrorCode.PERMISSION_DENY_ERROR);
        }
        for (Integer role : userDetail.getRoles()) {
            RoleDetailRes roleDetail = uuvClient.roleDetail(role.toString());
            UserDetail.Role r = new UserDetail.Role();
            r.setRoleId(roleDetail.getRoleId());
            r.setRoleCode(roleDetail.getRoleCode());
            r.setRoleName(roleDetail.getRoleName());
            ud.getRoles().add(r);
        }
        return ud;
    }

    /**
     * 根据父级 递归获取学校id
     *
     * @param parentIds
     * @param schools
     */
    private void getOrgChildren(List<String> parentIds, List<String> schools) {
        List<OrganizationDO> children = organizationMapper.getChildrenByParents(parentIds);
        schools.addAll(children.stream().filter(v -> v.getSubType() == 4).map(OrganizationDO::getOrgIndex).collect(Collectors.toList()));
        if (children.stream().anyMatch(v -> v.getSubType() < 4)) {
            List<String> parents = children.stream().filter(v -> v.getSubType() < 4).map(OrganizationDO::getOrgIndex).collect(Collectors.toList());
            getOrgChildren(parents, schools);
        }
    }


    @PostConstruct
    public void initOrg2Name() {
        log.info("初始化加载组织id和名称--------------------------------");
        List<OrganizationDO> list = organizationMapper.selectOrgIndexAndName();
        orgIndex2NameMap.putAll(list.stream().collect(Collectors.toMap(OrganizationDO::getOrgIndex, OrganizationDO::getOrgName)));
        log.info("初始化加载组织id和名称,加载完成,共{}个组织--------------------------------", orgIndex2NameMap.size());
    }

    @PostConstruct
    public void initCity2School() throws InterruptedException {
        long time = System.currentTimeMillis();
        log.info("初始化加载市级下面的学校--------------------------------");
        //获取州市id
        List<OrganizationDO> childrenOrg = organizationMapper.selectBySubType(2);
        List<String> childrenOrgIndex = childrenOrg.stream().filter(v -> !v.getOrgName().contains("直属")).map(OrganizationDO::getOrgIndex).collect(Collectors.toList());
        CountDownLatch latch = new CountDownLatch(childrenOrgIndex.size());
        for (String orgIndex : childrenOrgIndex) {
            statisticsExecutor.execute(() -> {
                try {
                    List<String> list = new ArrayList<>();
                    getOrgChildren(CollUtil.newArrayList(orgIndex), list);
                    citySchoolMap.put(orgIndex, list);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        log.info("初始化加载市级下面的学校,加载完成.共{}个市,{}个学校,耗时:{}", citySchoolMap.size(), citySchoolMap.values().stream().mapToInt(List::size).sum(), DateUtil.between(new Date(System.currentTimeMillis()), new Date(time), DateUnit.SECOND));
    }

}
