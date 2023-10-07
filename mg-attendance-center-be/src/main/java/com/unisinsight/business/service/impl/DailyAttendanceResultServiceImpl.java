package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.*;
import com.unisinsight.business.common.enums.AdjustModeEnum;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.utils.DateRangeCalcUtil;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.dto.DailyAttendanceResultDTO;
import com.unisinsight.business.dto.ResultChangeRecordDTO;
import com.unisinsight.business.dto.StuWeekResultCountDTO;
import com.unisinsight.business.dto.request.AtsHistoryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceDetailQueryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceDetailReqDTO;
import com.unisinsight.business.dto.response.AtsHistoryResDTO;
import com.unisinsight.business.dto.response.DailyAttendanceDetailResDTO;
import com.unisinsight.business.mapper.DailyAttendanceResultMapper;
import com.unisinsight.business.mapper.DailyAttendanceWeekResultMapper;
import com.unisinsight.business.model.DailyAttendanceResultDO;
import com.unisinsight.business.model.DailyAttendanceWeekResultDO;
import com.unisinsight.business.model.ResultChangeRecordDO;
import com.unisinsight.business.service.DailyAttendanceResultService;
import com.unisinsight.framework.common.util.date.DateUtils;
import com.unisinsight.framework.common.util.user.User;
import com.unisinsight.framework.common.util.user.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日常考勤明细查询
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
@Slf4j
@Service
public class DailyAttendanceResultServiceImpl extends AttendanceResultService implements DailyAttendanceResultService {

    @Resource
    private DailyAttendanceResultMapper dailyAttendanceResultMapper;

    @Resource
    private DailyAttendanceWeekResultMapper dailyAttendanceWeekResultMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchSave(LocalDate date, List<DailyAttendanceResultDO> results) {
        // 保存日考勤结果
        dailyAttendanceResultMapper.batchSave(results);

        // 所有的人员编号
        List<String> personNos = new ArrayList<>(results.size());
        // key: person_no, value: DailyAttendanceResultDO
        Map<String, DailyAttendanceResultDO> dayResultMap = new HashMap<>(results.size());
        for (DailyAttendanceResultDO result : results) {
            personNos.add(result.getPersonNo());
            dayResultMap.put(result.getPersonNo(), result);
        }

        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate friday = date.with(DayOfWeek.FRIDAY);

        // 查找已经有周考勤结果的记录
        List<DailyAttendanceWeekResultDO> weekResults = dailyAttendanceWeekResultMapper
                .findByPersonsOfWeek(personNos, monday);
        log.info("{} 个人员在 {} 周期内有 {} 条已生成的周考勤记录", personNos.size(), monday, weekResults.size());

        // 需要创建周考勤记录的人员
        List<String> needCreateWeekResultPersons;
        if (weekResults.isEmpty()) {
            needCreateWeekResultPersons = personNos;
        } else {
            // 已经生成了周考勤结果的人员
            Set<String> hasWeekResultPersons = weekResults
                    .stream()
                    .map(DailyAttendanceWeekResultDO::getPersonNo)
                    .collect(Collectors.toSet());
            needCreateWeekResultPersons = personNos
                    .stream()
                    .filter(personNo -> !hasWeekResultPersons.contains(personNo))
                    .collect(Collectors.toList());

            // 已经生成了周考勤结果的人员
            List<DailyAttendanceWeekResultDO> updatedWeekResults = weekResults
                    .stream()
                    .filter(weekResult -> updateWeekResult(dayResultMap.get(weekResult.getPersonNo()), weekResult))
                    .collect(Collectors.toList());

            if (!updatedWeekResults.isEmpty()) {
                dailyAttendanceWeekResultMapper.batchUpdate(updatedWeekResults);
                log.info("更新了 {} 条周考勤结果", updatedWeekResults.size());
            }
        }

        log.info("有 {} 个人员需要创建周考勤记录", needCreateWeekResultPersons.size());
        if (needCreateWeekResultPersons.isEmpty()) {
            return;
        }

        // 创建周考勤记录
        weekResults = needCreateWeekResultPersons
                .stream()
                .map(personNo -> generateWeekResult(dayResultMap.get(personNo), monday, friday))
                .collect(Collectors.toList());
        dailyAttendanceWeekResultMapper.batchSave(weekResults);
        log.info("创建了 {} 条周考勤记录", weekResults.size());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(DailyAttendanceResultDO record, AttendanceResult result) {
        record.setUpdatedAt(LocalDateTime.now());
        record.setResult(result.getType());
        dailyAttendanceResultMapper.updateByPrimaryKeySelective(record);
        updateWeekResult(record);
    }

    /**
     * 根据日考勤结果生成周考勤结果
     */
    private DailyAttendanceWeekResultDO generateWeekResult(DailyAttendanceResultDO dayResult,
                                                           LocalDate monday, LocalDate friday) {
        DailyAttendanceWeekResultDO weekResult;
        weekResult = new DailyAttendanceWeekResultDO();
        weekResult.setId(IdGenerateUtil.getId());
        weekResult.setPersonNo(dayResult.getPersonNo());
        weekResult.setPersonName(dayResult.getPersonName());
        weekResult.setOrgIndex(dayResult.getOrgIndex());
        weekResult.setOrgIndexPath(dayResult.getOrgIndexPath());
        weekResult.setCreatedAt(dayResult.getCreatedAt());
        // 考勤周期
        weekResult.setAttendanceStartDate(monday);
        weekResult.setAttendanceEndDate(friday);
        // 考勤结果
        int dayOfWeek = dayResult.getAttendanceDate().getDayOfWeek().getValue();
        if (dayOfWeek == DayOfWeek.MONDAY.getValue()) {
            weekResult.setResultOfMonday(dayResult.getResult());
        } else if (dayOfWeek == DayOfWeek.TUESDAY.getValue()) {
            weekResult.setResultOfTuesday(dayResult.getResult());
        } else if (dayOfWeek == DayOfWeek.WEDNESDAY.getValue()) {
            weekResult.setResultOfWednesday(dayResult.getResult());
        } else if (dayOfWeek == DayOfWeek.THURSDAY.getValue()) {
            weekResult.setResultOfThursday(dayResult.getResult());
        } else if (dayOfWeek == DayOfWeek.FRIDAY.getValue()) {
            weekResult.setResultOfFriday(dayResult.getResult());
        }

        if (dayResult.getResult() == AttendanceResult.NORMAL.getType() ||
                dayResult.getResult() == AttendanceResult.LEAVE.getType() ||
                dayResult.getResult() == AttendanceResult.PRACTICE.getType()) {
            // 只要有一次是正常，周考勤就是正常
            weekResult.setResult(AttendanceResult.NORMAL.getType());
        } else {
            weekResult.setResult(AttendanceResult.ABSENCE.getType());
        }
        return weekResult;
    }

    /**
     * 更新周考勤记录
     */
    private void updateWeekResult(DailyAttendanceResultDO result) {
        LocalDate monday = result.getAttendanceDate().with(DayOfWeek.MONDAY);
        LocalDate friday = result.getAttendanceDate().with(DayOfWeek.FRIDAY);

        DailyAttendanceWeekResultDO weekResult = dailyAttendanceWeekResultMapper
                .findByPersonOfWeek(result.getPersonNo(), monday);
        if (weekResult == null) {
            // 考勤周结果为空，新建
            weekResult = generateWeekResult(result, monday, friday);
            dailyAttendanceWeekResultMapper.insert(weekResult);
        } else {
            // 考勤周结果不为空，更新
            if (updateWeekResult(result, weekResult)) {
                dailyAttendanceWeekResultMapper.updateResult(weekResult);
            }
        }
    }

    /**
     * 根据日考勤结果更新周考勤结果，如果有更新返回 true
     */
    private boolean updateWeekResult(DailyAttendanceResultDO dayResult, DailyAttendanceWeekResultDO weekResult) {
        int dayOfWeek = dayResult.getAttendanceDate().getDayOfWeek().getValue();
        Integer result = dayResult.getResult();

        boolean updated = false;
        if (dayOfWeek == DayOfWeek.MONDAY.getValue()) {
            if (!result.equals(weekResult.getResultOfMonday())) {
                weekResult.setResultOfMonday(result);
                updated = true;
            }
        } else if (dayOfWeek == DayOfWeek.TUESDAY.getValue()) {
            if (!result.equals(weekResult.getResultOfTuesday())) {
                weekResult.setResultOfTuesday(result);
                updated = true;
            }

        } else if (dayOfWeek == DayOfWeek.WEDNESDAY.getValue()) {
            if (!result.equals(weekResult.getResultOfWednesday())) {
                weekResult.setResultOfWednesday(result);
                updated = true;
            }

        } else if (dayOfWeek == DayOfWeek.THURSDAY.getValue()) {
            if (!result.equals(weekResult.getResultOfThursday())) {
                weekResult.setResultOfThursday(result);
                updated = true;
            }

        } else if (dayOfWeek == DayOfWeek.FRIDAY.getValue()) {
            if (!result.equals(weekResult.getResultOfFriday())) {
                weekResult.setResultOfFriday(result);
                updated = true;
            }
        }

        if (updated) {
            if (dayResult.getResult() == AttendanceResult.NORMAL.getType() ||
                    dayResult.getResult() == AttendanceResult.LEAVE.getType() ||
                    dayResult.getResult() == AttendanceResult.PRACTICE.getType()) {
                // 更新周考勤结果为正常
                weekResult.setResult(AttendanceResult.NORMAL.getType());
            } else if (isRestWeek(weekResult)) {
                // 如果本周都是休息，那么考勤结果也算作正常
                weekResult.setResult(AttendanceResult.NORMAL.getType());
            }
            weekResult.setUpdatedAt(LocalDateTime.now());
            log.debug("更新周考勤结果 {}", weekResult.getId());
        } else {
            log.info("周考勤结果 {} 未更新", weekResult.getId());
        }
        return updated;
    }

    /**
     * 判断本周是不是都是休息
     *
     * @param weekResult 周考勤结果
     * @return 如果周一 - 周五都是休息，返回 true
     */
    private boolean isRestWeek(DailyAttendanceWeekResultDO weekResult) {
        return weekResult.getResultOfMonday() != null &&
                weekResult.getResultOfMonday() == AttendanceResult.REST.getType() &&
                weekResult.getResultOfTuesday() != null &&
                weekResult.getResultOfTuesday() == AttendanceResult.REST.getType() &&
                weekResult.getResultOfWednesday() != null &&
                weekResult.getResultOfWednesday() == AttendanceResult.REST.getType() &&
                weekResult.getResultOfThursday() != null &&
                weekResult.getResultOfThursday() == AttendanceResult.REST.getType() &&
                weekResult.getResultOfFriday() != null &&
                weekResult.getResultOfFriday() == AttendanceResult.REST.getType();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateResults(String personNo, LocalDate startDate, LocalDate endDate, AttendanceResult result,
                              String comment, AdjustModeEnum mode) {
        // 查询待更新记录
        Example example = Example.builder(DailyAttendanceResultDO.class)
                .where(Sqls.custom()
                        .andEqualTo("personNo", personNo)
                        .andGreaterThanOrEqualTo("attendanceDate", startDate)
                        .andLessThanOrEqualTo("attendanceDate", endDate)
                )
                .build();
        List<DailyAttendanceResultDO> records = dailyAttendanceResultMapper.selectByCondition(example);
        log.info("[调整日常考勤] - 找到 {} 在 {} - {} 的 {} 条记录", personNo, startDate, endDate, records.size());

        if (records.isEmpty()) {
            // 生成考勤记录
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        User user = UserHandler.getUser();

        List<ResultChangeRecordDO> changeRecords = new ArrayList<>(records.size());
        for (DailyAttendanceResultDO record : records) {
            ResultChangeRecordDO changeRecord = new ResultChangeRecordDO();
            changeRecord.setAttendanceResultId(record.getId());
            changeRecord.setResultBeforeChange(record.getResult());
            changeRecord.setResultAfterChange(result.getType());
            changeRecord.setComment(comment);
            changeRecord.setMode(mode.getValue());
            changeRecord.setChangedAt(now);
            changeRecord.setChangedBy(user == null ? "" : user.getUserCode());
            changeRecords.add(changeRecord);

            // 更新考勤结果
            update(record, result);
        }

        // 保存变更记录
        resultChangeRecordMapper.insertList(changeRecords);

        log.info("[调整日常考勤] - 更新了 {} 的 {} 条记录", personNo, records.size());
    }

    @Override
    public void updateOrCreateResult(String personNo, LocalDate attendanceDate, AttendanceResult result,
                                     String comment, AdjustModeEnum mode) {
        // 查询待更新记录
        Example example = Example.builder(DailyAttendanceResultDO.class)
                .where(Sqls.custom()
                        .andEqualTo("personNo", personNo)
                        .andEqualTo("attendanceDate", attendanceDate)
                )
                .build();
        List<DailyAttendanceResultDO> results = dailyAttendanceResultMapper.selectByCondition(example);

        LocalDateTime now = LocalDateTime.now();
        User user = UserHandler.getUser();

        if (results.isEmpty()) {
            // 当天不存在考勤记录，新生成
            log.info("{} 在 {} 不存在考勤记录，新生成", personNo, attendanceDate);
            PersonBO person = personMapper.findPersonBO(personNo);

            DailyAttendanceResultDO dayResult = new DailyAttendanceResultDO();
            dayResult.setId(IdGenerateUtil.getId());
            dayResult.setResult(result.getType());
            dayResult.setAttendanceDate(attendanceDate);
            dayResult.setPersonNo(person.getPersonNo());
            dayResult.setPersonName(person.getPersonName());
            dayResult.setOrgIndex(person.getOrgIndex());
            dayResult.setOrgIndexPath(person.getOrgIndexPath());// 加上人员所在组织本级
            dayResult.setCreatedAt(now);
            dailyAttendanceResultMapper.batchSave(Collections.singletonList(dayResult));

            updateWeekResult(dayResult);
        } else {
            // 当天存在考勤记录
            DailyAttendanceResultDO dayResult = results.get(0);
            int beforeResult = dayResult.getResult();
            log.info("{} 在 {} 存在考勤记录，更新", personNo, dayResult.getId());

            update(dayResult, result);

            // 保存变更记录
            ResultChangeRecordDO changeRecord = new ResultChangeRecordDO();
            changeRecord.setAttendanceResultId(dayResult.getId());
            changeRecord.setResultBeforeChange(beforeResult);
            changeRecord.setResultAfterChange(result.getType());
            changeRecord.setComment(comment);
            changeRecord.setMode(mode.getValue());
            changeRecord.setChangedAt(now);
            changeRecord.setChangedBy(user == null ? "" : user.getUserCode());
            resultChangeRecordMapper.insertUseGeneratedKeys(changeRecord);

            log.info("[调整日常考勤] - 更新了 {} 在 {} 的考勤记录", personNo, attendanceDate);
        }
    }

    @Override
    public PaginationRes<DailyAttendanceResultDTO> query(DailyAttendanceDetailQueryReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());

        DateRangeBO dateRange = DateRangeCalcUtil.calc(req.getSchoolYear(), req.getSemester(),
                req.getMonth());
        log.info("根据学年：{}，学期：{}，月份：{} 计算出日期范围：{}", req.getSchoolYear(), req.getSemester(),
                req.getMonth(), dateRange);

        List<DailyAttendanceWeekResultDO> records = dailyAttendanceWeekResultMapper.query(dateRange.getStartDate(),
                dateRange.getEndDate(), req.getPersonNo(), req.getResult(), req.getOrderByResultDesc());

        List<DailyAttendanceResultDTO> data = records.stream()
                .map(src -> {
                    DailyAttendanceResultDTO dst = new DailyAttendanceResultDTO();
                    dst.setId(String.valueOf(src.getId()));
                    dst.setResult(src.getResult());
                    dst.setPersonNo(src.getPersonNo());
                    dst.setPersonName(src.getPersonName());
                    dst.setResultOfMonday(src.getResultOfMonday());
                    dst.setResultOfTuesday(src.getResultOfTuesday());
                    dst.setResultOfWednesday(src.getResultOfWednesday());
                    dst.setResultOfThursday(src.getResultOfThursday());
                    dst.setResultOfFriday(src.getResultOfFriday());

                    LocalDate startDate = src.getAttendanceStartDate();
                    LocalDate endDate = src.getAttendanceEndDate();
                    dst.setAttendanceStartDate(startDate.format(DateUtils.DATE_FORMATTER));
                    dst.setAttendanceEndDate(endDate.format(DateUtils.DATE_FORMATTER));

                    int yearOfStartDate = startDate.getYear();
                    int yearOfEndDate = endDate.getYear();
                    int monthOfStartDate = startDate.getMonthValue();
                    int monthOfEndDate = endDate.getMonthValue();

                    // 学年，不用算了取前端传的值
                    dst.setSchoolYear(req.getSchoolYear());

                    // 计算学期，秋季学期日期为当年9.1～次年1.31；春季学期日期为次年3.1～次年7.31
                    if (monthOfStartDate >= 3 && monthOfStartDate <= 7) {
                        dst.setSemester("春季");
                    } else {
                        dst.setSemester("秋季"); // TODO 学期计算
                    }

                    // 计算月份
                    String startMonth = yearOfStartDate + "年" + monthOfStartDate + "月";
                    String endMonth = yearOfEndDate + "年" + monthOfEndDate + "月";
                    if (startMonth.equals(endMonth)) {
                        dst.setMonth(startMonth);
                    } else {
                        dst.setMonth(startMonth + " - " + endMonth);
                    }
                    return dst;
                })
                .collect(Collectors.toList());
        return PaginationRes.of(data, page);
    }

    @Override
    public DailyAttendanceDetailResDTO getDetails(DailyAttendanceDetailReqDTO req) {
        // 查找考勤记录
        Example example = Example.builder(DailyAttendanceResultDO.class)
                .where(Sqls.custom()
                        .andEqualTo("personNo", req.getPersonNo())
                        .andEqualTo("attendanceDate", req.getAttendanceDate())
                )
                .orderByDesc("id")
                .build();
        List<DailyAttendanceResultDO> results = dailyAttendanceResultMapper.selectByCondition(example);
        if (results.isEmpty()) {
            throw new InvalidParameterException("考勤记录不存在");
        }

        DailyAttendanceResultDO result = results.get(0);
        DailyAttendanceDetailResDTO res = new DailyAttendanceDetailResDTO();

        res.setResult(result.getResult());
        res.setAttendanceDate(result.getAttendanceDate().format(DateUtils.DATE_FORMATTER));
        LocalDateTime attendanceTime = result.getCapturedAt() != null ? result.getCapturedAt() : result.getCreatedAt();
        res.setAttendanceTime(attendanceTime.format(DateUtils.DATETIME_FORMATTER));

        // 查找人员信息
        res.setPersonInfo(personMapper.findByPersonNo(result.getPersonNo()));

        if (AttendanceResult.LEAVE.getType() == result.getResult()) {
            // 如果是请假，查找请假信息
            res.setLeaveInfo(leaveRecordMapper.findByPersonAtDate(result.getPersonNo(),
                    result.getAttendanceDate()));
        } else if (AttendanceResult.PRACTICE.getType() == result.getResult()) {
            // 如果是实习，查找实习信息
            res.setPracticeInfo(practiceRecordMapper.findByPersonAtDate(result.getPersonNo(),
                    result.getAttendanceDate()));
        }

        // 查找最近抓拍记录
        res.setRecentRecords(getRecentCaptureRecords(result.getPersonNo(), result.getCapturedAt()));

        // 查找考勤变更记录
        example = Example.builder(ResultChangeRecordDO.class)
                .where(Sqls.custom()
                        .andEqualTo("attendanceResultId", result.getId())
                )
                .orderByDesc("id")
                .build();
        List<ResultChangeRecordDO> changeRecords = resultChangeRecordMapper.selectByCondition(example);
        res.setChangeRecords(new ArrayList<>(changeRecords.size() + 1));

        // 原始考勤
        ResultChangeRecordDTO originalRecord = new ResultChangeRecordDTO();
        originalRecord.setChangedAt(result.getCreatedAt().format(DateUtils.DATETIME_FORMATTER));
        originalRecord.setMode(AdjustModeEnum.ORIGINAL_RESULT.getValue());
        res.getChangeRecords().add(0, originalRecord);

        if (changeRecords.isEmpty()) {
            // 未调整过，就是当前结果
            originalRecord.setResult(result.getResult());
        } else {
            // 原始考勤结果是第一次调整前的结果
            originalRecord.setResult(changeRecords.get(0).getResultBeforeChange());

            for (ResultChangeRecordDO changeRecord : changeRecords) {
                ResultChangeRecordDTO dst = new ResultChangeRecordDTO();
                dst.setResult(changeRecord.getResultAfterChange());
                dst.setComment(changeRecord.getComment());
                dst.setChangedAt(changeRecord.getChangedAt().format(DateUtils.DATETIME_FORMATTER));
                dst.setMode(changeRecord.getMode());
                res.getChangeRecords().add(dst);
            }
        }
        return res;
    }

    @Override
    public AtsHistoryResDTO getHistoryByPerson(AtsHistoryReqDTO req) {
        AtsHistoryResDTO res = new AtsHistoryResDTO();

        // 查询考勤历史
        LocalDate startDate = DateUtils.fromMilliseconds(req.getStartTime()).toLocalDate();
        LocalDate endDate = DateUtils.fromMilliseconds(req.getEndTime()).toLocalDate();

        List<HistoryItem> records = dailyAttendanceResultMapper.findHistoryOfPerson(req.getPersonNo(),
                startDate, endDate);
        res.setRecords(records);

        // 统计每种类型的天数
        Map<Integer, Integer> cntMap = new HashMap<>();
        for (HistoryItem record : records) {
            Integer cnt = cntMap.get(record.getResult());
            cntMap.put(record.getResult(), cnt == null ? 1 : cnt + 1);
        }

        // 总天数
        int total = records.size();

        // 统计概况
        List<HistoryStatistics> statistics = cntMap.entrySet()
                .stream()
                .map(entry -> {
                    HistoryStatistics stat = new HistoryStatistics();
                    stat.setResult(entry.getKey());
                    stat.setCount(entry.getValue());
                    stat.setPercent(((float) entry.getValue() * 100) / total);
                    return stat;
                }).collect(Collectors.toList());

        res.setStatistics(statistics);
        return res;
    }

    @Override
    public List<StuWeekResultCountDTO> findStuWeekResultList(FindStuWeekResultParamBO param) {
        return dailyAttendanceWeekResultMapper.findStuWeekResultList(param);
    }
}
