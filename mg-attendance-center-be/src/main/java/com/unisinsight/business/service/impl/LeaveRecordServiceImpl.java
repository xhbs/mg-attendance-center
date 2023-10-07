package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.common.enums.*;
import com.unisinsight.business.common.utils.UserCache;
import com.unisinsight.business.common.utils.UserStruct;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.LeaveInfoDTO;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.request.LeaveAddReqDTO;
import com.unisinsight.business.dto.request.LeaveRecordQueryReqDTO;
import com.unisinsight.business.dto.request.LeaveUpdateReqDTO;
import com.unisinsight.business.dto.response.LeaveRecordDetailDTO;
import com.unisinsight.business.dto.response.LeaveRecordListDTO;
import com.unisinsight.business.mapper.LeaveRecordMapper;
import com.unisinsight.business.model.ApproveRecordDO;
import com.unisinsight.business.model.LeaveRecordDO;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.service.LeaveRecordService;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.date.DateUtils;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 请假管理服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Slf4j
@Service
public class LeaveRecordServiceImpl extends ApprovalRecordService implements LeaveRecordService {

    @Resource
    private LeaveRecordMapper leaveRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Integer> add(LeaveAddReqDTO req) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = UserCache.getUserStruct();

        HashSet<String> roleSet = user.getRoleSet();
        if (roleSet == null) {
            throw new InvalidParameterException("用户未分配角色");
        }

        if (!roleSet.contains(AdminRole.HEAD_TEACHER.getCode())) {
            throw new InvalidParameterException("用户不是班主任，无法发起请假流程");
        }

        // 校验时间参数
        LocalDate startDate = DateUtils.fromMilliseconds(req.getStartTime()).toLocalDate();
        LocalDate endDate = DateUtils.fromMilliseconds(req.getEndTime()).toLocalDate();
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("请假开始日期不能大于结束日期");
        }

        // 查询请假人员列表
        List<PersonOfClassBO> persons = getPersonNos(req.getPersonNos());

        // 检查该时间段内是否有实习记录、请假记录
        checkRepeat(req.getPersonNos(), startDate, endDate);

        // 根据当前用户的org_index查找所属学校
        OrganizationDO school = user.getSchool();
        if (school == null) {
            throw new BaseException("当前用户不属于任何学校");
        }
        LocalDateTime now = LocalDateTime.now();
        // 生成请假记录
        List<LeaveRecordDO> records = persons
                .stream()
                .map(person -> {
                    LeaveRecordDO record = new LeaveRecordDO();
                    record.setType(req.getType());
                    record.setStartDate(startDate);
                    record.setEndDate(endDate);
                    record.setReason(req.getReason() == null ? "" : req.getReason());
                    record.setPersonNo(person.getPersonNo());
                    record.setPersonName(person.getPersonName());
                    record.setOrgIndex(person.getOrgIndex());
                    record.setOrgName(person.getOrgName());
                    record.setStatus(ApprovalStatus.UN_REPORT.getValue());
                    // 所有记录的层级保存到校级
                    record.setSchoolName(school.getOrgName());
                    record.setOrgIndexPath(school.getIndexPath() + school.getOrgIndex());
                    record.setOrgPathName(school.getOrgName() + "<" + school.getIndexPathName());
                    record.setCreatorCode(user.getUserCode());
                    record.setCreatorName(user.getUserName());
                    record.setCreatorRoleName(AdminRole.HEAD_TEACHER.getName());
                    record.setCreatedAt(now);
                    return record;
                }).collect(Collectors.toList());
        // 批量保存
        leaveRecordMapper.insertList(records);

        // 生成的ID列表
        List<Integer> ids = new ArrayList<>(records.size());
        for (LeaveRecordDO record : records) {
            ids.add(record.getId());
            // 保存附件文件
            saveAttachFiles(record.getId(), RecordType.LEAVE, req.getFiles());
        }

        // 是否同时上报
        if (req.isReport()) {
            reportRecords(records, user);
        }

        log.info("[请假管理] - {} 发起了请假流程：{}", user.getUserName(), ids);
        return ids;
    }

    /**
     * 开始上报，班主任申请，学校审批
     *
     * @param records 请假记录列表
     * @param user    管理员
     */
    private void reportRecords(List<LeaveRecordDO> records, UserStruct user) {
        // 获取当前用户的管理员角色
        for (LeaveRecordDO record : records) {
            // 保存审批流程
            List<ApproveRecordDO> approveRecords = new ArrayList<>(2);
            approveRecords.add(ApproveRecordDO.builder()
                    .serialNo(1)
                    .targetId(record.getId())
                    .targetType(RecordType.LEAVE.getValue())
                    .name("班主任申请")
                    .result(ApprovalResult.PASS.getValue())
                    .assigneeUserCode(user.getUserCode())
                    .assigneeUserName(user.getUserName())
                    .assigneeRoleCode(AdminRole.HEAD_TEACHER.getCode())
                    .assigneeRoleName(AdminRole.HEAD_TEACHER.getName())
                    .assignedAt(LocalDateTime.now())
                    .build());
            approveRecords.add(ApproveRecordDO.builder()
                    .serialNo(2)
                    .targetId(record.getId())
                    .targetType(RecordType.LEAVE.getValue())
                    .name("学校审批")
                    .assigneeRoleCode(AdminRole.SCHOOL.getCode())
                    .assigneeRoleName(AdminRole.SCHOOL.getName())
                    .build());
            approveRecordMapper.batchSave(approveRecords);

            record.setStatus(ApprovalStatus.UN_PROCESS.getValue());
            record.setReportedAt(LocalDateTime.now());
            leaveRecordMapper.updateByPrimaryKeySelective(record);
            log.info("[请假管理] - {} 上报了记录: {}", user.getUserName(), record.getId());
        }
    }

    @Override
    public void update(Integer id, LeaveUpdateReqDTO req) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        // 查找要更新的记录
        LeaveRecordDO record = leaveRecordMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new InvalidParameterException("记录不存在");
        }

        // 只有创建人本人才可修改
        if (!record.getCreatorCode().equals(user.getUserCode())) {
            throw new InvalidParameterException("非当前用户所创建，无法编辑");
        }

        // 只有未上报的记录可更新
        if (ApprovalStatus.UN_REPORT.getValue() != record.getStatus()) {
            throw new InvalidParameterException("请假已上报，无法更新");
        }

        // 校验时间参数
        LocalDate startDate = DateUtils.fromMilliseconds(req.getStartTime()).toLocalDate();
        LocalDate endDate = DateUtils.fromMilliseconds(req.getEndTime()).toLocalDate();
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("请假开始日期不能大于结束日期");
        }

        // 更新请假参数
        record.setType(req.getType());
        record.setStartDate(startDate);
        record.setEndDate(endDate);
        record.setReason(req.getReason());
        record.setUpdatedAt(LocalDateTime.now());
        leaveRecordMapper.updateByPrimaryKeySelective(record);

        // 删除之前所有关联的文件
        deleteAttachFiles(record.getId(), RecordType.LEAVE);

        // 保存新的附件文件
        saveAttachFiles(record.getId(), RecordType.LEAVE, req.getFiles());

        // 是否同时上报
        if (req.isReport()) {
            reportRecords(Collections.singletonList(record), user);
        }

        log.info("[请假管理] - {} 更新了请假流程：{}", user.getUserName(), record.getId());
    }

    @Override
    public LeaveRecordDetailDTO get(Integer id) {
        // 根据主键查询记录
        LeaveRecordDetailDTO record = leaveRecordMapper.findById(id);
        if (record == null) {
            throw new InvalidParameterException("记录不存在");
        }

        // 查询流程列表
        record.setProcesses(getProcesses(id, RecordType.LEAVE.getValue()));

        // 查询关联的文件列表
        record.setFiles(getAttachFiles(record.getId(), RecordType.LEAVE));
        return record;
    }

    @Override
    public PaginationRes<LeaveRecordListDTO> list(LeaveRecordQueryReqDTO req) {
        setUserIndexPath(req);

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<LeaveRecordListDTO> records = leaveRecordMapper.findByConditions(req);
        return PaginationRes.of(records, page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void report(List<Integer> ids) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        // 查询请假记录列表
        Example example = Example.builder(LeaveRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", ids))
                .build();
        List<LeaveRecordDO> records = leaveRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("记录不存在");
        }

        for (LeaveRecordDO record : records) {
            // 判断记录是否已上报
            if (record.getStatus() != ApprovalStatus.UN_REPORT.getValue()) {
                throw new InvalidParameterException("包含已上报的记录: " + record.getPersonName());
            }

            record.setStatus(ApprovalStatus.UN_PROCESS.getValue());
            record.setReportedAt(LocalDateTime.now());
            leaveRecordMapper.updateByPrimaryKeySelective(record);
        }

        reportRecords(records, user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(ApprovalReqDTO req) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        HashSet<String> roleSet = user.getRoleSet();
        if (roleSet == null) {
            throw new InvalidParameterException("未分配角色，无权审批");
        }

        if (!roleSet.contains(AdminRole.SCHOOL.getCode())) {
            throw new InvalidParameterException("不是校级资助管理员，无权审批");
        }

        String userIndexPath = user.getIndexPath();
        if (userIndexPath == null) {
            throw new InvalidParameterException("用户未关联到组织");
        }

        // 查询请假记录列表
        Example example = Example.builder(LeaveRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", req.getIds())
                )
                .build();
        List<LeaveRecordDO> records = leaveRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("记录不存在");
        }

        for (LeaveRecordDO record : records) {
            if (record.getStatus() != ApprovalStatus.UN_PROCESS.getValue()) {
                throw new InvalidParameterException("包含不可审核的记录");
            }

            // 检查是否有审批权限
            if (!record.getOrgIndexPath().contains(userIndexPath)) {
                throw new InvalidParameterException("包含没有处理权限的记录");
            }

            // 查找审批流程记录
            example = Example.builder(ApproveRecordDO.class)
                    .where(Sqls.custom()
                            .andEqualTo("targetId", record.getId())
                            .andEqualTo("targetType", RecordType.LEAVE.getValue())
                            .andEqualTo("serialNo", 2)
                    )
                    .build();
            List<ApproveRecordDO> approveRecords = approveRecordMapper.selectByCondition(example);
            if (!approveRecords.isEmpty()) {
                ApproveRecordDO approveRecord = approveRecords.get(0);
                approveRecord.setResult(req.getResult());
                approveRecord.setComment(req.getComment());
                approveRecord.setAssigneeUserCode(user.getUserCode());
                approveRecord.setAssigneeUserName(user.getUserName());
                approveRecord.setAssignedAt(LocalDateTime.now());
                approveRecordMapper.updateByPrimaryKeySelective(approveRecord);
            } else {
                log.error("{} 的审批流程不存在", record.getId());
            }

            if (req.getResult().equals(ApprovalResult.PASS.getValue())) {
                // 通过
                record.setStatus(ApprovalStatus.PASSED.getValue());
                record.setLeaveState(calcLeaveState(record).getValue());

                // 审批通过，调整日常考勤
                dailyAttendanceResultService.updateResults(record.getPersonNo(), record.getStartDate(),
                        record.getEndDate(), AttendanceResult.LEAVE, req.getComment(), AdjustModeEnum.SYSTEM_CHANGE);
                // 调整抽查考勤
                taskResultService.updateResults(record.getPersonNo(), record.getStartDate(),
                        record.getEndDate(), AttendanceResult.LEAVE, req.getComment(), AdjustModeEnum.SYSTEM_CHANGE);
            } else {
                // 拒绝
                record.setStatus(ApprovalStatus.REJECTED.getValue());
            }

            record.setApprovedBy(user.getUserName());
            leaveRecordMapper.updateByPrimaryKeySelective(record);
        }

        log.info("{} 审批了请假流程 {}，审批结果: {}", user.getUserCode(), req.getIds(), req.getResult());
    }

    /**
     * 根据请假的时间计算请假状态
     */
    private LeaveState calcLeaveState(LeaveRecordDO record) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = record.getStartDate();
        LocalDate endDate = record.getEndDate();
        if (now.isBefore(startDate)) {
            // 开始日期小于当天开始日期，未生效
            return LeaveState.NOT_BEGIN;
        }

        if (now.isAfter(endDate)) {
            // 结束日期小于当天开始日期，请假结束
            return LeaveState.FINISHED;
        }
        // 请假中
        return LeaveState.LEAVING;
    }

    @Override
    public void delete(List<Integer> ids) {
        User user = UserUtils.getUser();
        Example example = Example.builder(LeaveRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", ids))
                .build();

        List<LeaveRecordDO> records = leaveRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("请假记录不存在");
        }

        for (LeaveRecordDO record : records) {
            if (record.getStatus() != ApprovalStatus.UN_REPORT.getValue()) {
                throw new InvalidParameterException("只有未上报的记录可删除");
            }
            if (!record.getCreatorCode().equals(user.getUserCode())) {
                throw new InvalidParameterException("不能删除非本人提交的记录");
            }
        }

        leaveRecordMapper.deleteByCondition(example);
        // 删除附件文件
        batchDeleteAttachFiles(ids, RecordType.LEAVE);

        log.info("[请假管理] - {} 删除了请假记录: {}", user.getUserName(), ids);
    }

    @Override
    public LeaveInfoDTO selectByPersonNoAndDate(String personNo, LocalDate date){
        return leaveRecordMapper.findByPersonAtDate(personNo,date);
    }
}
