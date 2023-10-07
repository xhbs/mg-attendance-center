package com.unisinsight.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.*;
import com.unisinsight.business.common.constants.Constants;
import com.unisinsight.business.common.enums.*;
import com.unisinsight.business.common.utils.*;
import com.unisinsight.business.dto.PersonDTO;
import com.unisinsight.business.dto.TaskResultExpandDto;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.dto.response.AttachFileDTO;
import com.unisinsight.business.dto.response.SpotCheckTaskDTO;
import com.unisinsight.business.manager.FMSManager;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.*;
import com.unisinsight.business.rpc.InfraClient;
import com.unisinsight.business.rpc.dto.RoleInfo;
import com.unisinsight.business.service.*;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import java.security.InvalidParameterException;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * 抽查考勤任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
@Service
@Slf4j
public class SpotCheckTaskServiceImpl implements SpotCheckTaskService {

    @Resource
    private SpotCheckTaskMapper spotCheckTaskMapper;

    @Resource
    private TaskOrgRelationMapper taskOrgRelationMapper;

    @Resource
    private TaskAttendanceDateMapper taskAttendanceDateMapper;

    @Resource
    private DailyAttendanceExcludeDateMapper dailyAttendanceExcludeDateMapper;

    @Resource
    private OrganizationMapper organizationMapper;

    @Resource
    private InfraClient infraClient;

    @Resource
    private FMSManager fmsManager;

    @Resource
    private TaskResultMapper taskResultMapper;

    @Resource
    private PersonMapper personMapper;

    @Resource
    private LeaveRecordService leaveRecordService;

    @Resource
    private PracticeRecordService practiceRecordService;

    @Resource
    private TaskPersonRelationService taskPersonRelationService;

    @Resource
    private TaskPersonRelationMapper taskPersonRelationMapper;

    @Resource
    private DailyAttendanceSettingService attendanceSettingService;
    @Resource
    private TransferDataUtils transferDataUtils;

    @Resource
    private TaskResultExpandDao taskResultExpandDao;

    @Resource
    private DailyAttendanceService dailyAttendanceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer add(SpotCheckTaskAddReqDTO req) {
        // 校验用户是否登录
        UserStruct user = UserCache.getUserStruct();

        // 校验是否有创建权限
        RoleInfo roleInfo = user.getHighestRole();
        if (roleInfo == null) {
            throw new InvalidParameterException("没有操作权限");
        }
        AdminRole role = AdminRole.valueOfCode(roleInfo.getRoleCode());
        if (role == null || role.getLevel() >= AdminRole.SCHOOL.getLevel()) {
            throw new InvalidParameterException("没有操作权限");
        }
        // 校验日期参数
        checkDateParameters(req);

        // 校验任务名称
        checkTaskName(null, req.getName(), user.getUserCode());

        // 保存任务参数
        SpotCheckTaskDO task = new SpotCheckTaskDO();
        setTaskParameters(req, user, task);
        task.setCreatedAt(LocalDateTime.now());
        spotCheckTaskMapper.insertUseGeneratedKeys(task);

        // 保存关联的组织
        saveTaskOrgRelations(task, req.getTargetOrgIndexes());

        // 随机生成考勤日期
        saveAttendanceDates(task);

        // 保存关联的人员
        saveTaskPersons(task.getId(), req);


        log.info("{} 创建了抽查考勤任务：{}", user.getUserCode(), task);
        return task.getId();
    }

    @Override
    public SpotTaskResultCountBO selectSpotPersonCount(int taskId) {
        UserStruct userStruct = UserCache.getUserStruct();
        if (userStruct.getRoles().stream().noneMatch(v -> v.getRoleCode().equals(AdminRole.HEAD_TEACHER.getCode()))) {
            throw new ValidationException("没有操作权限!");
        }
        SpotTaskResultCountBO bo = taskResultMapper.selectSpotPersonCount(taskId, userStruct.getClassesIndex().get(0));
        bo.setNumOfFinish(bo.getNumOfTotal() - bo.getNumOfNull());
        return bo;
    }

    /**
     * 新建点名
     *
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer addCallTheRoll(SpotCheckTaskAddReqDTO req) {

        UserStruct user = UserCache.getUserStruct();

        // 校验是否有创建权限
        RoleInfo roleInfo = user.getHighestRole();

        if (roleInfo == null) {
            throw new InvalidParameterException("没有操作权限");
        }
        AdminRole role = AdminRole.valueOfCode(roleInfo.getRoleCode());
        if (role == null || role.getLevel() > AdminRole.CITY.getLevel()) {
            throw new InvalidParameterException("没有操作权限");
        }

        if (req.getStartDate().getMonth().compareTo(req.getEndDate().getMonth()) != 0) {
            throw new ValidationException("开始时间和结束时间必须在同一个月!");
        }
        if (req.getEndDate().getDayOfMonth() > 15 && req.getTest() == null && req.getAuto().compareTo(Boolean.TRUE) != 0) {
            throw new ValidationException("结束时间必须在15号之前!");
        }
        StaticDateBO staticDateBO = transferDataUtils.getStaticDateBO(LocalDate.now());

        //点名只抽查一天
        req.setDayCount(1);
        req.setCallTheRoll(Constants.TRUE);
        req.setMinimumAbsenceRate(0);
        req.setSchoolYear(staticDateBO.getSchoolYear());
        req.setSemester(staticDateBO.getSchoolTerm().equals("0") ? "春季" : "秋季");
        req.setMonth(staticDateBO.getYearMonth());

        StringBuilder sb = new StringBuilder();
        OrganizationDO organizationDO = organizationMapper.selectByOrgIndex(req.getTargetOrgIndexes().get(0));
        //点名任务名称
        sb.append(organizationDO.getOrgName()).append(req.getStartDate().getMonth().getValue()).append("月");
        if (req.getTest() != null) {
            sb.append("_点名测试_")
                    .append(NanoId.randomNanoId(5));
        } else {
            if (req.getEndDate().getDayOfMonth() < 16 && req.getAuto().compareTo(Boolean.TRUE) != 0) {
                sb.append("_上半月点名");
            } else {
                sb.append("_下半月点名");
            }
        }
        req.setName(sb.toString());
        return add(req);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SpotCheckTaskUpdateReqDTO req) {
        // 校验用户是否登录
        UserStruct user = UserCache.getUserStruct();

        SpotCheckTaskDO task = spotCheckTaskMapper.selectByPrimaryKey(req.getId());
        if (task == null) {
            throw new InvalidParameterException("任务不存在");
        }

        // 只有本用户创建的任务才支持编辑
        if (!task.getCreatorCode().equals(user.getUserCode())) {
            throw new InvalidParameterException("不是本人创建的任务，不能修改");
        }

        // 只有未开始的任务才支持编辑
        if (task.getState() != SpotCheckTaskState.NOT_BEGIN.getValue()) {
            throw new InvalidParameterException("不是未开始的任务，不能修改");
        }

        // 校验日期参数
        checkDateParameters(req);

        // 校验任务名称
        checkTaskName(task.getId(), req.getName(), user.getUserCode());

        // 考勤周期是否发送了变化
        boolean attendanceDateUpdated = !(req.getStartDate().isEqual(task.getStartDate()) && req.getEndDate().isEqual(task.getEndDate()));

        // 保存任务参数
        setTaskParameters(req, user, task);
        task.setUpdatedAt(LocalDateTime.now());
        spotCheckTaskMapper.updateByPrimaryKeySelective(task);

        // 删除关联的组织
        taskOrgRelationMapper.deleteByCondition(Example.builder(TaskOrgRelationDO.class).where(Sqls.custom().andEqualTo("taskId", task.getId())).build());
        // 保存关联的组织
        saveTaskOrgRelations(task, req.getTargetOrgIndexes());

        if (attendanceDateUpdated) {
            // 删除关联的考勤日期
            taskAttendanceDateMapper.deleteByCondition(Example.builder(TaskAttendanceDateDO.class).where(Sqls.custom().andEqualTo("taskId", task.getId())).build());
            // 随机生成考勤日期
            saveAttendanceDates(task);
            log.info("抽查任务 {} 考勤日期修改，重新生成考勤日期", task.getId());
        } else {
            log.info("抽查任务 {} 考勤日期未修改，不用重新生成考勤日期", task.getId());
        }

        // 删除关联的人员
        taskPersonRelationService.deleteByTaskIds(Collections.singletonList(req.getId()));
        // 保存关联的人员
        saveTaskPersons(task.getId(), req);

        log.info("{} 修改了抽查考勤任务：{}", user.getUserCode(), task);
    }

    /**
     * 校验日期参数，生成抽查任务
     */
    private void checkDateParameters(SpotCheckTaskAddReqDTO req) {
        // 校验时间参数
        LocalDate today = LocalDate.now();
        LocalDate startDate = req.getStartDate();
        LocalDate endDate = req.getEndDate();
        if (startDate.isBefore(today)) {
            throw new InvalidParameterException("开始日期不能晚于今天");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("开始日期不能晚于结束日期");
        }

        // 校验抽查天数
        int days = (int) startDate.until(endDate, ChronoUnit.DAYS) + 1;
        if (req.getDayCount() > days) {
            throw new InvalidParameterException("抽查天数不能大于" + days + "天");
        }
    }

    /**
     * 校验任务名称，同一个人不能建重复的考勤任务名称
     */
    private void checkTaskName(Integer taskId, String taskName, String userCode) {
        Sqls sqls = Sqls.custom().andEqualTo("name", taskName).andEqualTo("creatorCode", userCode);
        if (taskId != null) {
            sqls.andNotEqualTo("id", taskId);
        }
        int count = spotCheckTaskMapper.selectCountByCondition(Example.builder(SpotCheckTaskDO.class).where(sqls).build());
        if (count > 0) {
            throw new InvalidParameterException("同一用户所创建的任务名称重复");
        }
    }

    /**
     * 保存任务参数
     */
    private void setTaskParameters(SpotCheckTaskAddReqDTO req, UserStruct user, SpotCheckTaskDO task) {
        task.setName(req.getName());
        task.setStartDate(req.getStartDate());
        task.setEndDate(req.getEndDate());
        task.setDayCount(req.getDayCount());
        task.setSchoolYear(req.getSchoolYear());
        task.setSemester(req.getSemester());
        task.setMonth(req.getMonth());
        task.setMinimumAbsenceRate(req.getMinimumAbsenceRate());
        task.setCreatorCode(user.getUserCode());
        task.setCreatorName(user.getUserName());
        task.setCreatorOrgIndexPath(user.getIndexPath() + user.getOrgIndex());
        task.setCreatorOrgName(user.getOrgName());
        task.setCreatorRoleName(user.getHighestRole().getRoleName());
        task.setState(calcTaskState(task).getValue()); // 根据日期计算状态
        if (req.getCallTheRoll().compareTo(Constants.TRUE) == 0) {
            task.setCallTheRoll(req.getCallTheRoll()); // 是否是点名任务
            task.setCallTheRollFirstTaskId(req.getCallTheRollFirstTaskId()); // 上半月点名任务id
        }
    }

    /**
     * 保存考勤目标组织关联关系
     */
    private void saveTaskOrgRelations(SpotCheckTaskDO task, List<String> targetOrgIndexes) {
        // 校验考勤目标
        Example example = Example.builder(OrganizationDO.class).where(Sqls.custom().andIn("orgIndex", targetOrgIndexes)).build();
        List<OrganizationDO> organizations = organizationMapper.selectByCondition(example);
        if (organizations.size() < targetOrgIndexes.size()) {
            throw new InvalidParameterException("包含不存在的考勤目标");
        }

        taskOrgRelationMapper.insertList(targetOrgIndexes.stream().map(orgIndex -> {
            TaskOrgRelationDO rel = new TaskOrgRelationDO();
            rel.setTaskId(task.getId());
            rel.setOrgIndex(orgIndex);
            return rel;
        }).collect(toList()));
    }

    /**
     * 随机生成考勤日期并保存
     */
    private void saveAttendanceDates(SpotCheckTaskDO task) {
        LocalDate startDate = task.getStartDate();
        LocalDate endDate = task.getEndDate();
        int targetDayCount = task.getDayCount();
        int totalDayCount = (int) startDate.until(endDate, ChronoUnit.DAYS) + 1;// 包括结束日期

        List<LocalDate> dates = new ArrayList<>(totalDayCount);
        for (int i = 0; i < totalDayCount; i++) {
            dates.add(startDate.plusDays(i));
        }
        log.info("考勤日期范围从 {} - {}，一共 {} 天", startDate, endDate, totalDayCount);

        DailyAttendanceSettingDO setting = attendanceSettingService.getSetting();
        if (setting != null) {
            if (setting.getExcludeWeekends()) {
                dates = dates.stream().filter(date -> date.getDayOfWeek().getValue() < DayOfWeek.SATURDAY.getValue()).collect(toList());
                log.info("配置排除周末，排除后还剩 {} 天", dates.size());

                if (dates.size() < targetDayCount) {
                    throw new InvalidParameterException("考勤日期排除周末后不足" + targetDayCount + "天");
                }
            }

            if (setting.getExcludeHolidays()) {
                dates = dailyAttendanceExcludeDateMapper.filterExcludeDates(ExcludeDateType.HOLIDAY.getValue(), dates);
                log.info("配置排除法定节假日，排除后还剩 {} 天", dates.size());

                if (dates.size() < targetDayCount) {
                    throw new InvalidParameterException("考勤日期排除法定节假日后不足" + targetDayCount + "天");
                }
            }

            if (setting.getExcludeCustomDates()) {
                dates = dailyAttendanceExcludeDateMapper.filterExcludeDates(ExcludeDateType.CUSTOM_DATE.getValue(), dates);
                log.info("配置排除自定义节假日，排除后还剩 {} 天", dates.size());

                if (dates.size() < targetDayCount) {
                    throw new InvalidParameterException("考勤日期排除自定义节假日后不足" + targetDayCount + "天");
                }
            }
        }

        // 有效的日期天数
        int validDayCount = dates.size();
        // 随机生成在 0 - validDayCount 之间的 targetDayCount 个不重复的数
        Random random = new Random();
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < targetDayCount) {
            int num = random.nextInt(validDayCount);
            numbers.add(num);
        }

        List<TaskAttendanceDateDO> records = numbers.stream().map(dates::get).sorted().map(date -> {
            TaskAttendanceDateDO rel = new TaskAttendanceDateDO();
            rel.setTaskId(task.getId());
            rel.setAttendanceDate(date);
            return rel;
        }).collect(toList());
        taskAttendanceDateMapper.insertList(records);

        log.info("任务 {} 的考勤周期 {} - {} 一共 {} 天，随机生成了 {} 个考勤日期", task.getId(), startDate, endDate, totalDayCount, dates.size());
    }

    /**
     * 保存任务与人员关联关系
     */
    private void saveTaskPersons(Integer taskId, SpotCheckTaskAddReqDTO req) {
        //点名任务和抽查任务 保存人员的逻辑不同
        if (req.getCallTheRoll().equals(1)) {
            saveTaskPersonsCtr(taskId, req);
            return;
        }
        DateRangeBO dateRange = DateRangeCalcUtil.calc(req.getSchoolYear(), req.getSemester(), req.getMonth());
        LocalDate startDate = dateRange.getStartDate();
        LocalDate endDate = dateRange.getEndDate();
        log.info("根据学年：{}，学期：{}，月份：{} 计算出日期范围：{} - {}", req.getSchoolYear(), req.getSemester(), req.getMonth(), startDate, endDate);

        // 查询考勤目标的所有班级
        List<String> classes = organizationMapper.selectLowerOrgIndex(req.getTargetOrgIndexes(), OrgType.CLASS.getValue());
        log.info("查询出组织 {} 下的 {} 个班级", req.getTargetOrgIndexes(), classes.size());

        if (classes.isEmpty()) {
            throw new InvalidParameterException("未查询到考勤目标下的班级，无法将任务与人员关联");
        }

        taskPersonRelationService.saveTaskPersons(taskId, classes, startDate, endDate, req.getMinimumAbsenceRate());
    }

    /**
     * 保存点名的人员名单
     */
    private void saveTaskPersonsCtr(Integer taskId, SpotCheckTaskAddReqDTO req) {
        // 查询考勤目标的所有班级
        List<String> classes = organizationMapper.selectLowerOrgIndex(req.getTargetOrgIndexes(), OrgType.CLASS.getValue());

        if (classes.isEmpty()) {
            throw new InvalidParameterException("未查询到考勤目标下的班级，无法将任务与人员关联");
        }

        log.info("查询出组织 {} 下的 {} 个班级", req.getTargetOrgIndexes(), classes.size());
        taskPersonRelationService.saveTaskPersonsCtr(taskId, classes, req.getCallTheRollFirstTaskId());
    }

    /**
     * 根据当前日期计算任务状态
     */
    private SpotCheckTaskState calcTaskState(SpotCheckTaskDO record) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = record.getStartDate();
        LocalDate endDate = record.getEndDate();
        if (now.isBefore(startDate)) {
            // 开始日期小于当天开始日期，未开始
            return SpotCheckTaskState.NOT_BEGIN;
        }

        if (now.isAfter(endDate)) {
            // 结束日期小于当天开始日期，已结束
            return SpotCheckTaskState.FINISHED;
        }
        // 运行中
        return SpotCheckTaskState.RUNNING;
    }

    @Override
    public SpotCheckTaskDTO get(Integer id) {
        SpotCheckTaskDTO task = spotCheckTaskMapper.findById(id);
        if (task == null) {
            throw new InvalidParameterException("任务不存在");
        }
        return task;
    }

    @Override
    public List<String> getAttendanceDates(Integer id) {
        return taskAttendanceDateMapper.findByTask(id);
    }

    @Override
    public PaginationRes<SpotCheckTaskDTO> list(SpotCheckTaskListReqDTO req) {
        DateRangeBO range = DateRangeCalcUtil.calc(req.getSchoolYear(), req.getSemester(), req.getMonth());
        req.setStartDate(range.getStartDate());
        req.setEndDate(range.getEndDate());

        UserStruct user = UserCache.getUserStruct();
        AdminRole adminRole = user.getAdminRoleCode();
        if (adminRole == null) {
            throw new InvalidParameterException("没有查询权限");
        }
        req.setAdminLevel(adminRole.getLevel());

        String userOrgIndexPath = user.getIndexPath() + user.getOrgIndex();
        req.setUserOrgIndexPath(userOrgIndexPath);
        req.setUserOrgIndexes(Arrays.asList(userOrgIndexPath.split("/")));

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SpotCheckTaskDTO> data = spotCheckTaskMapper.list(req);
        return PaginationRes.of(data, page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        // 登录校验
        User user = UserUtils.mustGetUser();

        Example example = Example.builder(SpotCheckTaskDO.class).where(Sqls.custom().andIn("id", ids)).build();
        List<SpotCheckTaskDO> tasks = spotCheckTaskMapper.selectByCondition(example);
        if (tasks.isEmpty()) {
            throw new InvalidParameterException("任务不存在");
        }

        for (SpotCheckTaskDO task : tasks) {
            // 只有本用户创建的任务才能删除
            if (!task.getCreatorCode().equals(user.getUserCode())) {
                throw new InvalidParameterException("不是本人创建的任务，不能删除");
            }

            // 只有未开始的任务才能删除
            if (task.getState() != SpotCheckTaskState.NOT_BEGIN.getValue()) {
                throw new InvalidParameterException("不是未开始的任务，不能删除");
            }
        }

        // 删除关联的组织
        taskOrgRelationMapper.deleteByCondition(Example.builder(TaskOrgRelationDO.class).where(Sqls.custom().andIn("taskId", ids)).build());

        // 删除关联的考勤日期
        taskAttendanceDateMapper.deleteByCondition(Example.builder(TaskAttendanceDateDO.class).where(Sqls.custom().andIn("taskId", ids)).build());

        // 删除关联的人员
        taskPersonRelationService.deleteByTaskIds(ids);

        spotCheckTaskMapper.deleteByCondition(example);
        log.info("{} 删除了抽查考勤任务: {}", user.getUserCode(), ids);
    }

    /**
     * 点名
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void callTheRoll(CallTheRollDto req) {

        //TODO 暂时兼容有感考勤 2023-03-30
        if (ObjectUtil.isEmpty(req.getTaskId())) {
            log.info("进行有感考勤逻辑");
            CallTheRollDto dto = BeanUtil.copyProperties(req, CallTheRollDto.class);
            dailyAttendanceService.updateAttend(dto);
            return;
        }

        UserStruct user = UserCache.getUserStruct();

        // 校验是否有创建权限
        List<RoleInfo> roleInfo = user.getRoles();
        if (roleInfo == null) {
            throw new InvalidParameterException("没有操作权限");
        }
        if (roleInfo.isEmpty() || roleInfo.stream().noneMatch(v -> v.getRoleCode().equals(AdminRole.HEAD_TEACHER.getCode()))) {
            throw new InvalidParameterException("没有点名权限!");
        }

        Example example = Example.builder(TaskResultDO.class).where(Sqls.custom().andEqualTo("taskId", req.getTaskId()).andEqualTo("personNo", req.getPersonNo())).build();
        List<TaskResultDO> resultDOS = taskResultMapper.selectByCondition(example);
        if (CollUtil.isNotEmpty(resultDOS)) {
            throw new InvalidParameterException("不能重复点名!");
        }

        Integer type = req.getType();
        PersonDTO person = personMapper.findByPersonNo(req.getPersonNo());

        if (type == AttendanceResult.LEAVE.getType()) {
            leave(req);
            req.setType(AttendanceResult.ABSENCE.getType());
        } else if (type == AttendanceResult.PRACTICE.getType()) {
            practice(req);
            req.setType(AttendanceResult.ABSENCE.getType());
        } else if (type == AttendanceResult.NORMAL.getType()) {
            if (req.getLocation() == null || req.getLocation().isEmpty() || req.getPlaceName() == null || req.getPlaceName().isEmpty()) {
                log.info("缺少在校数据: backType: " + req.getBackType() + " | location: " + req.getLocation() + " | placeName: " + req.getPlaceName());
                throw new BaseException("提交失败");
            }
        }

        TaskResultDO resultDO = generateLeaveDto(req, person);
        taskResultMapper.batchSave(Collections.singletonList(resultDO));
        TaskResultExpand expand = generateExpand(req, resultDO);
        taskResultExpandDao.insert(expand);
    }

    private TaskResultExpand generateExpand(CallTheRollDto req, TaskResultDO resultDO) {
        TaskResultExpand expand = new TaskResultExpand();
        expand.setTaskResultId(resultDO.getId());
        expand.setLocation(req.getLocation());
        expand.setCreateTime(System.currentTimeMillis());
        expand.setPlaceName(req.getPlaceName());
        expand.setAbsenceReason(req.getAbsenceReason());
        if (StrUtil.isNotBlank(req.getPicture())) {
            expand.setPicture(fmsManager.uploadImageBase64(req.getPicture()));
        }
        expand.setMatchResult(req.getMatchResult());
        return expand;
    }

    /**
     * 请假流程
     *
     * @param req
     */
    private void leave(CallTheRollDto req) {
        if (req.getLeaveType() == null || req.getStartTime() == null || req.getEndTime() == null || req.getLeaveImg() == null) {
            log.info("缺少请假数据,请假失败: type: " + req.getLeaveType() + " | startTime: " + req.getStartTime().toString() + " | endTime: " + req.getEndTime() + " | leaveImg: " + req.getLeaveImg());
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
    }

    /**
     * 实习
     */
    private void practice(CallTheRollDto req) {
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
        practiceRecordService.add(practice);
    }

    private TaskResultDO generateLeaveDto(CallTheRollDto task, PersonDTO person) {
        TaskResultDO result = new TaskResultDO();
        result.setId(IdGenerateUtil.getId());
        result.setTaskId(task.getTaskId());
        result.setResult(task.getType());
        result.setAttendanceDate(LocalDate.now());
        result.setCapturedAt(LocalDateTime.now());
        result.setOriginalRecordId(null);
        result.setPersonNo(person.getPersonNo());
        result.setPersonName(person.getPersonName());
        result.setOrgIndex(person.getOrgIndex());
        result.setCreatedAt(LocalDateTime.now());
        return result;
    }

    @Override
    public PaginationRes<PersonListOfClassBO> stuList(StuListReq req) {
        UserStruct user = UserCache.getUserStruct();
        if (user.getRoles().stream().noneMatch(v -> v.getRoleCode().equals(AdminRole.HEAD_TEACHER.getCode()))) {
            throw new ValidationException("没有操作权限!");
        }
        Page<PersonListOfClassBO> page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PersonListOfClassBO> stus = taskPersonRelationMapper.findPersonListByTaskId(req, user.getClassesIndex());

//        stus.sort(Comparator.comparingInt(PersonListOfClassBO::getStatus).reversed().thenComparing(PersonOfClassBO::getPersonNo));

        return PaginationRes.of(stus, page);
    }

    @Override
    public TaskResultExpandDto detail(StuListReq req) {
        PersonListOfClassBO bo = stuList(req).getData().get(0);
        TaskResultExpandDto dto = new TaskResultExpandDto();
        BeanUtil.copyProperties(bo, dto);
        if (ObjectUtil.isNotEmpty(bo.getTaskResultId())) {
            TaskResultExpand expand = taskResultExpandDao.selectByPrimaryKey(bo.getTaskResultId());
            BeanUtil.copyProperties(expand, dto);
            if (ObjectUtil.isEmpty(expand) || ObjectUtil.isEmpty(expand.getTaskResultId())) {
                TaskResultDO taskResultDO = taskResultMapper.selectByPrimaryKey(bo.getTaskResultId());
                if(!(ObjectUtil.isEmpty(taskResultDO) || ObjectUtil.isEmpty(taskResultDO.getTaskId()))){
                    if(ObjectUtil.isEmpty(taskResultDO.getCapturedAt())){
                        dto.setCreateTime(taskResultDO.getCreatedAt().toInstant(ZoneOffset.of("+8")).toEpochMilli());
                    }else{
                        dto.setCreateTime(taskResultDO.getCapturedAt().toInstant(ZoneOffset.of("+8")).toEpochMilli());
                    }
                }
            }
        }
        if (bo.getResult().compareTo(AttendanceResult.PRACTICE.getType()) == 0) {
            dto.setPractice(practiceRecordService.selectDetailByPersonNo(bo.getPersonNo(), LocalDate.now()));
        }
        if (bo.getResult().compareTo(AttendanceResult.LEAVE.getType()) == 0) {
            dto.setLeave(leaveRecordService.selectByPersonNoAndDate(bo.getPersonNo(), LocalDate.now()));
        }

        return dto;
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }
}
