package com.unisinsight.business.service.impl;

import com.unisinsight.business.bo.ChannelOfSchoolBO;
import com.unisinsight.business.bo.PersonOfSchoolBO;
import com.unisinsight.business.mapper.PersonMapper;
import com.unisinsight.business.service.AttendanceEventFilter;
import com.unisinsight.business.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 考勤人员打卡间隔限制
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/3/4
 * @since 1.0
 */
@Component
@Slf4j
public class AttendanceEventFilterImpl implements AttendanceEventFilter {

    /**
     * 打卡间隔限制，单位ms
     */
    @Value("${attendance-result-handler.disposition-event-interval-limit:0}")
    private int dispositionEventIntervalLimit;

    /**
     * key: person_no, value: 上一次打卡时间、学校的 org_index
     */
    private static volatile ConcurrentHashMap<String, PersonOfSchoolBO> PERSON_CACHE;

    /**
     * key: 学校的 org_index, value: 学校的所有设备通道ID
     */
    private static volatile ConcurrentHashMap<String, Set<String>> DEVICE_OF_SCHOOL_CACHE;

    @Resource
    private PersonMapper personMapper;

    @Resource
    private DeviceService deviceService;

    @Override
    public void refreshPersonCache() {
        PERSON_CACHE = null;
        initPersonCache();
    }

    /**
     * 初始化人员缓存
     */
    private synchronized void initPersonCache() {
        if (PERSON_CACHE != null) {
            log.info("人员缓存已加载");
            return;
        }

        List<PersonOfSchoolBO> allPersons = personMapper.findPersonsOfSchool(null);
        PERSON_CACHE = new ConcurrentHashMap<>(allPersons.size());
        for (PersonOfSchoolBO person : allPersons) {
            PERSON_CACHE.put(person.getPersonNo(), person);
        }
    }

    @Override
    public void addPersonCache(List<String> personNos) {
        if (PERSON_CACHE == null) {
            initPersonCache();
        } else {
            List<PersonOfSchoolBO> persons = personMapper.findPersonsOfSchool(personNos);
            for (PersonOfSchoolBO person : persons) {
                PERSON_CACHE.put(person.getPersonNo(), person);
            }
        }
    }

    @Override
    public void removePersonCache(List<String> personNos) {
        if (PERSON_CACHE != null) {
            for (String personNo : personNos) {
                PERSON_CACHE.remove(personNo);
            }
        }
    }

    @Override
    public void refreshChannelCache() {
        DEVICE_OF_SCHOOL_CACHE = null;
        initChannelCache();
    }

    /**
     * 初始化通道缓存
     */
    private synchronized void initChannelCache() {
        if (DEVICE_OF_SCHOOL_CACHE != null) {
            log.info("通道缓存已加载");
            return;
        }

        long startedAt = System.currentTimeMillis();
        List<ChannelOfSchoolBO> allChannels = deviceService.findAllChannels();
        if (allChannels.isEmpty()) {
            log.warn("未查询到学校关联的通道");
            return;
        }

        DEVICE_OF_SCHOOL_CACHE = new ConcurrentHashMap<>(allChannels.size());

        // 按照学校分组
        allChannels.stream()
                .collect(Collectors.groupingBy(ChannelOfSchoolBO::getUserOrgIndex))
                .forEach((orgIndex, channels) -> DEVICE_OF_SCHOOL_CACHE.put(orgIndex, channels.stream()
                        .map(ChannelOfSchoolBO::getChannelId)
                        .collect(Collectors.toSet())));
        log.info("加载 {} 个通道缓存，耗时 {} ms", allChannels.size(), System.currentTimeMillis() - startedAt);
    }

    @Override
    public boolean checkAttendanceEvent(String personNo, String deviceCode, long eventTime) {
        if (PERSON_CACHE == null) {
            initPersonCache();
        }
        if (DEVICE_OF_SCHOOL_CACHE == null) {
            initChannelCache();
        }

        PersonOfSchoolBO person = PERSON_CACHE.get(personNo);
        if (person == null) {
            log.info("{} 不是考勤人员", personNo);
            return false;
        }

        boolean effective = checkFrequency(person, eventTime) &&
                checkLocation(person, deviceCode);
        if (effective) {
            // 保存本次有效打卡的时间
            person.setLatestEventTime(eventTime);
        }
        return effective;
    }

    /**
     * 校验打卡频率
     *
     * @return 如果距离上一次打卡的时间间隔大于配置的阈值返回 true
     */
    private boolean checkFrequency(PersonOfSchoolBO person, long eventTime) {
        if (dispositionEventIntervalLimit <= 0) {
            return true;
        }

        if (Math.abs(eventTime - person.getLatestEventTime()) < dispositionEventIntervalLimit) {
            log.info("{} 打卡间隔小于 {} ms，不记录考勤事件", person.getPersonNo(), dispositionEventIntervalLimit);
            return false;
        }
        return true;
    }

    /**
     * 校验是否在本校设备打卡
     *
     * @return 如果设备属于学生所在学校返回 true
     */
    private boolean checkLocation(PersonOfSchoolBO person, String deviceCode) {
        Set<String> channelSet = DEVICE_OF_SCHOOL_CACHE.get(person.getOrgIndexOfSchool());
        if (channelSet == null) {
            log.info("学校 {} 下没有设备", person.getOrgIndexOfSchool());
            return false;
        }

        if (!channelSet.contains(deviceCode)) {
            log.info("{} 的抓拍相机 {} 不是本校设备", person.getPersonNo(), deviceCode);
            return false;
        }
        return true;
    }
}
