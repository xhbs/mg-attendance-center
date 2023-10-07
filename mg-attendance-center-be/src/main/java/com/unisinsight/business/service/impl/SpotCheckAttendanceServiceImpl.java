package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.DateRangeBO;
import com.unisinsight.business.bo.LeaveInfo;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.enums.AdjustModeEnum;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.utils.DateRangeCalcUtil;
import com.unisinsight.business.common.utils.UserStruct;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.ResultChangeRecordDTO;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.dto.response.*;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.FeelAttendanceRecordsDO;
import com.unisinsight.business.model.ResultChangeRecordDO;
import com.unisinsight.business.model.SpotCheckTaskDO;
import com.unisinsight.business.model.TaskResultDO;
import com.unisinsight.business.rpc.InfraClient;
import com.unisinsight.business.service.SpotCheckAttendanceService;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 抽查考勤
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Service
@Slf4j
public class SpotCheckAttendanceServiceImpl extends AttendanceResultService implements SpotCheckAttendanceService {
    @Resource
    private OrganizationMapper organizationsMapper;

    @Resource
    private InfraClient infraClient;

    @Resource
    private SpotCheckTaskMapper spotCheckTaskMapper;

    @Resource
    private TaskResultStatMapper taskResultStatMapper;

    @Resource
    private TaskResultMapper taskResultMapper;

    @Resource
    private AttendanceRecordsMapper recordsMapper;

    @Override
    public PaginationRes<TaskResultStatResDTO> statistics(TaskResultStatReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<TaskResultStatResDTO> data = taskResultStatMapper.list(req);
        return PaginationRes.of(data, page);
    }

    @Override
    public PaginationRes<TaskResultListResDTO> list(TaskResultListReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<TaskResultListResDTO> data = taskResultMapper.list(req);
        return PaginationRes.of(data, page);
    }

    @Override
    public SpotAttendanceDetailResDTO getDetails(SpotAttendanceDetailReqDTO req) {
        // 查找考勤记录
        Example example = Example.builder(TaskResultDO.class)
                .where(Sqls.custom()
                        .andEqualTo("taskId", req.getTaskId())
                        .andEqualTo("personNo", req.getPersonNo())
                        .andEqualTo("attendanceDate", req.getAttendanceDate())
                )
                .orderByDesc("id")
                .build();
        List<TaskResultDO> results = taskResultMapper.selectByCondition(example);
        if (results.isEmpty()) {
            throw new InvalidParameterException("考勤记录不存在");
        }

        SpotAttendanceDetailResDTO res = new SpotAttendanceDetailResDTO();

        TaskResultDO result = results.get(0);
        res.setResult(result.getResult());
        res.setAttendanceDate(result.getAttendanceDate().format(DateUtils.DATE_FORMATTER));
        LocalDateTime attendanceTime = result.getCapturedAt() != null ? result.getCapturedAt() : result.getCreatedAt();
        res.setAttendanceTime(attendanceTime.format(DateUtils.DATETIME_FORMATTER));

        // 查找考勤任务
        SpotCheckTaskDO task = spotCheckTaskMapper.selectByPrimaryKey(result.getTaskId());
        SpotAttendanceDetailResDTO.TaskInfo taskInfo = new SpotAttendanceDetailResDTO.TaskInfo();
        taskInfo.setTaskId(task.getId());
        taskInfo.setTaskName(task.getName());
        taskInfo.setCreatorName(task.getCreatorName());
        taskInfo.setCreatorRoleName(task.getCreatorRoleName());
        taskInfo.setCreatorOrgName(task.getCreatorOrgName());
        res.setTaskInfo(taskInfo);

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
        res.setRecentRecords(getRecentCaptureRecords(result.getPersonNo(),
                result.getCapturedAt() == null ? result.getCreatedAt() : result.getCapturedAt()));

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
    public void exportCountExcel(TaskResultStatExportReqDTO req, HttpServletResponse resp) {
        List<TaskResultStatResDTO> data = taskResultStatMapper.list(req);
        List<SpotCheckCountExcelDTO> excelDTOList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TaskResultStatResDTO item = data.get(i);
            SpotCheckCountExcelDTO build = SpotCheckCountExcelDTO.builder()
                    .id(i + 1)
                    .schoolName(item.getSchoolOrgName())
                    .indexPathName(item.getIndexPathName())
                    .attentDate(item.getAttendanceDate())
                    .stuTotal(item.getStudentNum())
                    .absenceRate(item.getAbsenceRate() + "%")
                    .absenceNum(item.getAbsenceNum())
                    .inSchoolNum(item.getNormalNum())
                    .leaveNum(item.getLeaveNum())
                    .practiceNum(item.getPracticeNum())
                    .build();
            excelDTOList.add(build);
        }
        ExcelUtil.exportExcel(resp, SpotCheckCountExcelDTO.class, excelDTOList, req.getTitle());
    }

    @Override
    public void exportListExcel(TaskResultListExportReqDTO req, HttpServletResponse resp) {
        List<SpotCheckListExcelDTO> excelDTOList = new ArrayList<>();
        List<TaskResultListResDTO> taskResultListResDTOS = taskResultMapper.list(req);
        for (int i = 0; i < taskResultListResDTOS.size(); i++) {
            TaskResultListResDTO item = taskResultListResDTOS.get(i);
            SpotCheckListExcelDTO.SpotCheckListExcelDTOBuilder builder = SpotCheckListExcelDTO.builder();
            builder.status(AttendanceResult.getNameByType(item.getResult()));
            builder.id(i + 1).stuName(item.getPersonName()).stuNo(item.getPersonNo()).className(item.getClassName());
            excelDTOList.add(builder.build());
        }
        ExcelUtil.exportExcel(resp, SpotCheckListExcelDTO.class, excelDTOList, req.getTitle());
    }

    @Override
    public SpotCheckNumDTO checkNum() {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }
        return spotCheckTaskMapper.countTasksOfClasses(classOrgIndexes);
    }

    @Override
    public PaginationRes<SpotCheckAttendListDTO> getTasksOfClasses(SpotCheckAttendListReqDTO req) {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (StringUtils.isNotEmpty(req.getSchoolYear())) {
            DateRangeBO range = DateRangeCalcUtil.calc(req.getSchoolYear(), req.getSemester(), req.getMonth());
            startDate = range.getStartDate();
            endDate = range.getEndDate();
        }

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SpotCheckAttendListDTO> data = spotCheckTaskMapper.findTasksOfClasses(today, classOrgIndexes,
                req.getStatus(), startDate, endDate, req.getSearchKey());

        for (SpotCheckAttendListDTO task : data) {
            if (StringUtils.isNotEmpty(task.getSpotCheckedDateStr())) {
                task.setSpotCheckedDates(Arrays.asList(task.getSpotCheckedDateStr().split("、")));
            }
        }
        return PaginationRes.of(data, page);
    }

    @Override
    public PaginationRes<SpotCheckAttendDetailDTO> getHaveTaskStudents(SpotCheckAttendDetailReqDTO req) {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("班主任 {} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("用户未绑定班级");
        }

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SpotCheckAttendDetailDTO> data = spotCheckTaskMapper.findHaveTaskStudents(req.getTaskId(), req.getDay(),
                classOrgIndexes, req.getStatus(), req.getResult(), req.getSearchKey());
        return PaginationRes.of(data, page);
    }

    @Override
    public FeelAttendDetailDTO attendDetail(FeelAttendDetailReqDTO req) {
        FeelAttendDetailDTO res = new FeelAttendDetailDTO();

        TaskResultDO taskResult = taskResultMapper.findResult(req.getPersonNo(), req.getTaskId(),
                req.getDay(), req.getResult());
        if (taskResult == null) {
            throw new BaseException("抽查考勤结果不存在");
        }

        res.setCapturedAt(taskResult.getCapturedAt() == null ? taskResult.getCreatedAt() : taskResult.getCreatedAt());
        res.setResult(req.getResult());

        FeelAttendanceRecordsDO record = recordsMapper.findByPerson(req.getTaskId(), req.getDay(), req.getPersonNo());
        if (record != null) {
            Integer result = req.getResult();
            if (result == AttendanceResult.NORMAL.getType()) {//在校
                res.setLocation(record.getLocation());
                res.setPlaceName(record.getPlaceName());
                if (record.getMatchImage() != null && !record.getMatchImage().isEmpty() && record.getMatchResult() != null) {
                    res.setFeedBackType(0);
                    res.setMatchImage(record.getMatchImage());
                    res.setMatchResult(record.getMatchResult());
                } else {
                    res.setFeedBackType(1);
                    res.setComment(record.getComment());
                }
            }
            if (result == AttendanceResult.LEAVE.getType()) {//请假
                if (record.getLeaveRecordId() == null) {
                    log.warn("有感考勤的请假记录ID为空：{}", record.getId());
                    throw new BaseException("考勤详情有误");
                }
                LeaveInfo leaveInfo = leaveRecordMapper.findLeaveInfoById(record.getLeaveRecordId());
                if (leaveInfo == null) {
                    log.warn("请假记录不存在：{}", record.getLeaveRecordId());
                    throw new BaseException("请假记录不存在");
                }
                res.setLeaveType(leaveInfo.getType());
                res.setLeaveStartDate(leaveInfo.getStartDate());
                res.setLeaveEndDate(leaveInfo.getEndDate());
                res.setLeaveReason(leaveInfo.getReason());
            }
            if (result == AttendanceResult.ABSENCE.getType()) {//缺勤
                res.setAbsenceReason(record.getAbsenceReason());
            }
        }
        return res;
    }

}