package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.LeaveInfo;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.enums.AdjustModeEnum;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.common.utils.UserStruct;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.AttendWeekResultDTO;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.dto.response.*;
import com.unisinsight.business.manager.FMSManager;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.DailyAttendanceWeekResultDO;
import com.unisinsight.business.model.FeelAttendanceRecordsDO;
import com.unisinsight.business.model.FeelTaskRelationDO;
import com.unisinsight.business.rpc.InfraClient;
import com.unisinsight.business.rpc.dto.OrgTreeResDTO;
import com.unisinsight.business.service.*;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/16
 */
@Service
@Slf4j
public class DailyAttendanceServiceImpl implements DailyAttendanceService {

    @Resource
    private OrganizationMapper organizationsMapper;

    @Resource
    private InfraClient infraClient;

    @Resource
    private PersonMapper personMapper;

    @Resource
    private DailyAttendanceWeekResultMapper dailyAttendanceWeekResultMapper;

    @Resource
    private LeaveRecordMapper leaveMapper;

    @Resource
    private PracticeRecordMapper practiceMapper;

    @Resource
    private LeaveRecordService leaveRecordService;

    @Resource
    private AttendanceRecordsMapper attendanceRecordsMapper;

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Resource
    private FeelTaskRelationMapper feelTaskRelationMapper;

    @Resource
    private FMSManager fmsManager;

    @Resource
    private PracticeRecordService practiceRecordService;

    @Resource
    private TaskResultService taskResultService;

    @Resource
    private DailyAttendanceResultService dailyAttendanceResultService;

    @Override
    public DailyAttendanceDTO countStudents() {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(DayOfWeek.MONDAY);
        LocalDate endDate = today.with(DayOfWeek.FRIDAY);

        DailyAttendanceDTO res = dailyAttendanceWeekResultMapper.countStudents(classOrgIndexes, startDate, endDate);
        res.setStartDate(startDate);
        res.setEndDate(endDate);
        return res;
    }

    @Override
    public PaginationRes<StudentDTO> queryStudent(QueryStudentListDto req) {
        // 查询班主任的所属班级
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }

        // 查询在籍学生
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<StudentDTO> list = personMapper.findByClasses(classOrgIndexes, req.getSearchKey());
        return PaginationRes.of(list, page);
    }

    @Override
    public PaginationRes<AttendWeekResultDTO> queryInSchool(QueryStudentListDto req) {
        // 查询班主任的所属班级
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }

        Integer type = req.getType();
        // 查询在校学生
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<AttendWeekResultDTO> studentList = dailyAttendanceWeekResultMapper.findByClasses(classOrgIndexes,
                LocalDate.now().with(DayOfWeek.MONDAY), type, req.getSearchKey());
        if (CollectionUtils.isNotEmpty(studentList)) {
            for (AttendWeekResultDTO attend : studentList) {
                int dayCount = 0;
                if (type.equals(attend.getResultOfMonday())) {
                    dayCount++;
                }
                if (type.equals(attend.getResultOfTuesday())) {
                    dayCount++;
                }
                if (type.equals(attend.getResultOfWednesday())) {
                    dayCount++;
                }
                if (type.equals(attend.getResultOfThursday())) {
                    dayCount++;
                }
                if (type.equals(attend.getResultOfFriday())) {
                    dayCount++;
                }
                if (type == 0 && dayCount != 0) {
                    attend.setDescribe("在校 : " + dayCount + "天");
                } else if (type == 99 && dayCount != 0) {
                    attend.setDescribe("缺勤 : " + dayCount + "天");
                }
            }
        }
        return PaginationRes.of(studentList, page);
    }

    @Override
    public List<InSchoolStuDTO> queryAttend(String personNo) {
        // 查询班主任的所属班级
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<OrgTreeResDTO> classesList = user.getClassesList();
        if (CollectionUtils.isEmpty(classesList)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }

        List<String> classOrgIndexes = classesList.stream()
                .map(OrgTreeResDTO::getOrgIndex)
                .collect(Collectors.toList());
        //查询学生
        List<AttendWeekResultDTO> studentList = dailyAttendanceWeekResultMapper.findByClasses(classOrgIndexes,
                LocalDate.now().with(DayOfWeek.MONDAY), null, personNo);

        List<InSchoolStuDTO> inSchoolStuDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(studentList)) {
            LocalDate today = LocalDate.now();
            AttendWeekResultDTO attend = studentList.get(0);
            InSchoolStuDTO inSchoolStuDTO = new InSchoolStuDTO();
            inSchoolStuDTO.setPersonNo(attend.getPersonNo());
            inSchoolStuDTO.setPersonName(attend.getPersonName());
            inSchoolStuDTO.setPersonUrl(attend.getPersonUrl());
            inSchoolStuDTO.setStartDate(attend.getAttendanceStartDate());
            inSchoolStuDTO.setEndDate(attend.getAttendanceEndDate());
            Map<String, Integer> map = new HashMap<>();
            //存入星期一到星期五的日期,和考勤状态
            map.put(today.with(DayOfWeek.MONDAY).format(DateUtils.DATE_FORMATTER), attend.getResultOfMonday());
            map.put(today.with(DayOfWeek.TUESDAY).format(DateUtils.DATE_FORMATTER), attend.getResultOfTuesday());
            map.put(today.with(DayOfWeek.WEDNESDAY).format(DateUtils.DATE_FORMATTER), attend.getResultOfWednesday());
            map.put(today.with(DayOfWeek.THURSDAY).format(DateUtils.DATE_FORMATTER), attend.getResultOfThursday());
            map.put(today.with(DayOfWeek.FRIDAY).format(DateUtils.DATE_FORMATTER), attend.getResultOfFriday());
            inSchoolStuDTO.setAttendDetails(map);
            //存入班级名称
            if (CollectionUtils.isNotEmpty(classesList)) {
                for (OrgTreeResDTO statisticsClassDo : classesList) {
                    if (statisticsClassDo.getOrgIndex().equals(attend.getOrgIndex())) {
                        inSchoolStuDTO.setOrgName(statisticsClassDo.getOrgName());
                    }
                }
            }
            FeelAttendanceRecordsDO feelAttendanceRecordsDO = attendanceRecordsMapper.findByResultId(attend.getId(), personNo);
            inSchoolStuDTO.setCanFeelAttend(false);
            if (attend.getResult() == 99) {
                if (feelAttendanceRecordsDO == null) {
                    inSchoolStuDTO.setCanFeelAttend(true);
                } else if (feelAttendanceRecordsDO.getLeaveRecordId() != null) {
                    LeaveInfo leaveInfoById = leaveMapper.findLeaveInfoById(feelAttendanceRecordsDO.getLeaveRecordId());
                    if (leaveInfoById != null && leaveInfoById.getStatus() == 4) {
                        attendanceRecordsMapper.delete(feelAttendanceRecordsDO);
                        inSchoolStuDTO.setCanFeelAttend(true);
                    }
                }
            }
            inSchoolStuDTOS.add(inSchoolStuDTO);
        }
        return inSchoolStuDTOS;
    }

    @Override
    public PaginationRes<LeaveStudentDTO> queryLeave(QueryStudentListDto req) {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }
        log.info("查询 {} 的所属班级 {} 的请假学生", user.getUserCode(), classOrgIndexes);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(today.with(DayOfWeek.MONDAY));
        LocalDate endDate = today.with(today.with(DayOfWeek.FRIDAY));

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<LeaveStudentDTO> data = leaveMapper.findByClasses(classOrgIndexes, startDate, endDate, req.getSearchKey());
        return PaginationRes.of(data, page);
    }

    @Override
    public PaginationRes<PracticeStudentDTO> queryPractice(QueryStudentListDto req) {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationsMapper, infraClient);
        List<String> classOrgIndexes = user.getClassesIndex();
        if (CollectionUtils.isEmpty(classOrgIndexes)) {
            log.info("{} 未绑定班级", user.getUserCode());
            throw new InvalidParameterException("未绑定班级");
        }
        log.info("查询 {} 的所属班级 {} 的实习学生", user.getUserCode(), classOrgIndexes);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(today.with(DayOfWeek.MONDAY));
        LocalDate endDate = today.with(today.with(DayOfWeek.FRIDAY));

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PracticeStudentDTO> data = practiceMapper.findByClasses(classOrgIndexes, startDate, endDate,
                req.getSearchKey());
        return PaginationRes.of(data, page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttend(CallTheRollDto req) {
        // 考勤日期所在周的周一
        LocalDate monday = req.getAttendDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Integer type = req.getType();
        //考勤枚举
        AttendanceResult attendanceResult = null;
        //有感考勤DO
        FeelAttendanceRecordsDO recordsDO = new FeelAttendanceRecordsDO();
        recordsDO.setAttendanceDate(req.getAttendDate());
        recordsDO.setPersonNo(req.getPersonNo());
        //抽查考勤任务关联对象
        List<FeelTaskRelationDO> taskRelationDOS = new ArrayList<>();
        //查询该学生是否有抽查考勤任务
        List<Integer> tasks = taskPersonRelationMapper.findPersonTask(req.getAttendDate(), req.getPersonNo());
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Integer task : tasks) {
                FeelTaskRelationDO feelTaskRelationDO = new FeelTaskRelationDO();
                feelTaskRelationDO.setTaskId(task);
                taskRelationDOS.add(feelTaskRelationDO);
            }
        }
        //获取该考勤记录,如果有周考勤,存入DO
        DailyAttendanceWeekResultDO weekResult = dailyAttendanceWeekResultMapper.findByPersonOfWeek(req.getPersonNo(), monday);
        //校验是否可以有感考勤
        if (weekResult == null && CollectionUtils.isEmpty(tasks)) {
            log.info("未查询到周考勤或抽查考勤任务:" + "personNo:" + req.getPersonNo());
            throw new BaseException("未查询到周考勤或抽查考勤任务");
        }
        if (weekResult != null) {
            recordsDO.setResultId(weekResult.getId());
        }
        //请假添加一条请假记录
        if (type == AttendanceResult.LEAVE.getType()) {
            attendanceResult = AttendanceResult.LEAVE;
            if (req.getLeaveType() == null || req.getStartTime() == null || req.getEndTime() == null || req.getLeaveImg() == null) {
                log.info("缺少请假数据,请假失败: type: " + req.getLeaveType() + " | startTime: " + req.getStartTime().toString()
                        + " | endTime: " + req.getEndTime() + " | leaveImg: " + req.getLeaveImg());
                throw new BaseException("提交失败");
            }
            LeaveAddReqDTO leaveAddReqDTO = new LeaveAddReqDTO();
            leaveAddReqDTO.setType(req.getLeaveType());
            leaveAddReqDTO.setStartTime(req.getStartTime());
            leaveAddReqDTO.setEndTime(req.getEndTime());
            leaveAddReqDTO.setReason(req.getReason());
            List<String> stuList = new ArrayList<>();
            stuList.add(req.getPersonNo());
            leaveAddReqDTO.setPersonNos(stuList);
            leaveAddReqDTO.setReport(true);
            List<AttachFileDTO> files = new ArrayList<>(1);
            AttachFileDTO attachFileDTO = new AttachFileDTO();
            attachFileDTO.setFilePath(req.getLeaveImg());
            attachFileDTO.setFileName(req.getLeaveImg());
            files.add(attachFileDTO);
            leaveAddReqDTO.setFiles(files);
            List<Integer> add = leaveRecordService.add(leaveAddReqDTO);
            if (add.size() <= 0) {
                log.info("添加请假失败:" + leaveAddReqDTO.toString());
                throw new BaseException("提交请假流程失败");
            }
            recordsDO.setLeaveRecordId(add.get(0));
        } else if (type == AttendanceResult.PRACTICE.getType()) {
            attendanceResult = AttendanceResult.PRACTICE;
            if (req.getStartTime() == null || req.getEndTime() == null || req.getPracticeCompany() == null || req.getContactsPhone() == null || req.getCompanyContacts() == null) {
                log.info("缺少实习数据,实习失败: type: " + req.getLeaveType() + " | startTime: " + req.getStartTime().toString() + " | endTime: " + req.getEndTime() + " | getPracticeCompany: " + req.getPracticeCompany() + " | getContactsPhone: " + req.getContactsPhone() + " | getCompanyContacts: " + req.getCompanyContacts());
                throw new BaseException("提交失败");
            }
            PracticeAddReqDTO practice = new PracticeAddReqDTO();
            BeanUtils.copyProperties(req, practice);

            practice.setStartDate(LocalDateTime.ofInstant(new Date(req.getStartTime()).toInstant(), ZoneId.systemDefault()).toLocalDate());
            practice.setEndDate(LocalDateTime.ofInstant(new Date(req.getEndTime()).toInstant(), ZoneId.systemDefault()).toLocalDate());
            practice.setPersonNos(Collections.singletonList(req.getPersonNo()));
            practice.setReport(true);
            Integer add = practiceRecordService.add(practice);
            if (add <= 0) {
                log.info("添加实习失败:" + practice);
                throw new BaseException("提交实习流程失败");
            }
            recordsDO.setPracticeRecordId(add);
        } else if (type == AttendanceResult.NORMAL.getType()) {//在校
            attendanceResult = AttendanceResult.NORMAL;
            //修改当天考勤状态
            Integer backType = req.getBackType();
            if (backType == null || req.getLocation() == null || req.getLocation().isEmpty()
                    || req.getPlaceName() == null || req.getPlaceName().isEmpty()) {
                log.info("缺少在校数据: backType: " + backType + " | location: " + req.getLocation() + " | placeName: " + req.getPlaceName());
                throw new BaseException("提交失败");
            }
            recordsDO.setLocation(req.getLocation());
            recordsDO.setPlaceName(req.getPlaceName());
            if (backType == 0 && req.getMatchResult() != null && req.getPicture() != null && !req.getPicture().isEmpty()) {
                //图片上传对象存储
                String url = fmsManager.uploadImageBase64(req.getPicture());
                if (url == null || url.isEmpty()) {
                    log.info("图片上传失败");
                    throw new BaseException("上传图片失败");
                }
                recordsDO.setMatchImage(url);
                recordsDO.setMatchResult(req.getMatchResult());

            } else if (backType == 1) {
                recordsDO.setComment(req.getComment());
            } else {
                log.info("缺少在校提交类型相关数据");
                throw new BaseException("提交失败");
            }
        } else if (type == AttendanceResult.ABSENCE.getType()) {//缺勤
            attendanceResult = AttendanceResult.ABSENCE;
            recordsDO.setAbsenceReason(req.getAbsenceReason());
        }
        FeelAttendanceRecordsDO feelAttendanceRecordsDO = attendanceRecordsMapper.findByResultId(recordsDO.getResultId(), req.getPersonNo());
        if (feelAttendanceRecordsDO == null) {
            //添加有感考勤记录
            int id = attendanceRecordsMapper.insertFeel(recordsDO);
            //修改周考勤(指定日期才可修改)
            if (weekResult != null && req.getAttendDate().isEqual(LocalDate.now())) {
                // 调整考勤结果
                dailyAttendanceResultService.updateOrCreateResult(req.getPersonNo(), req.getAttendDate(),
                        attendanceResult, req.getComment(), AdjustModeEnum.MANUAL_CHANGE);
            }
            //添加或修改抽查考勤考勤结果(当天才能修改)
            if (CollectionUtils.isNotEmpty(taskRelationDOS) && req.getAttendDate().isEqual(LocalDate.now())) {
                for (FeelTaskRelationDO taskRelationDO : taskRelationDOS) {
                    taskRelationDO.setFeelId(id);
                    feelTaskRelationMapper.insert(taskRelationDO);
                    taskResultService.updateOrCreateResult(req.getPersonNo(), taskRelationDO.getTaskId()
                            , req.getAttendDate(), attendanceResult, req.getComment(), AdjustModeEnum.MANUAL_CHANGE);
                }
            }
        } else {
            log.info("当天已经提交过有感考勤不能重复提交");
            throw new BaseException("当天已经提交过有感考勤不能重复提交");
        }
    }
}
