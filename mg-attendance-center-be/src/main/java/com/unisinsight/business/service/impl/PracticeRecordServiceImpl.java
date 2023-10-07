package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.DictBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.common.enums.*;
import com.unisinsight.business.common.utils.UserCache;
import com.unisinsight.business.common.utils.UserStruct;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.request.PracticeAddReqDTO;
import com.unisinsight.business.dto.request.PracticeRecordQueryReqDTO;
import com.unisinsight.business.dto.response.PracticeRecordDetailDTO;
import com.unisinsight.business.dto.response.PracticeRecordListDTO;
import com.unisinsight.business.mapper.PracticePersonMapper;
import com.unisinsight.business.model.*;
import com.unisinsight.business.service.PracticeRecordService;
import com.unisinsight.business.service.SystemConfigService;
import com.unisinsight.framework.common.exception.BaseException;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实习管理服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@Slf4j
@Service
public class PracticeRecordServiceImpl extends ApprovalRecordService implements PracticeRecordService {

    @Resource
    private PracticePersonMapper practicePersonMapper;

    @Resource
    private SystemConfigService systemConfigService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer add(PracticeAddReqDTO req) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = UserCache.getUserStruct();

        HashSet<String> roleSet = user.getRoleSet();
        if (roleSet == null) {
            throw new InvalidParameterException("用户未分配角色");
        }

        if (!roleSet.contains(AdminRole.HEAD_TEACHER.getCode())) {
            throw new InvalidParameterException("不是班主任，无法发起实习申请");
        }

        // 校验时间参数
        LocalDate startDate = req.getStartDate();
        LocalDate endDate = req.getEndDate();
        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new InvalidParameterException("实习开始日期不能大于结束日期");
        }

//        Integer days = systemConfigService.getIntegerConfigValue(SystemConfigs.PRACTICE_SUBMIT_ADVANCE_DAYS);
//        if (startDate.minusDays(days == null ? 0 : days).isBefore(LocalDate.now())) {
//            throw new InvalidParameterException("需要在实习开始日期前" + days + "天发起申请");
//        }

        // 检查该时间段内是否有实习记录、请假记录
        checkRepeat(req.getPersonNos(), startDate, endDate);

        // 根据当前用户的org_index查找所属学校
        OrganizationDO school = user.getSchool();
        if (school == null) {
            throw new BaseException("当前用户不属于任何学校");
        }

        // 保存实习记录
        PracticeRecordDO record = new PracticeRecordDO();
        record.setStartDate(startDate);
        record.setEndDate(endDate);
        record.setPracticeCompany(req.getPracticeCompany());
        record.setCompanyContacts(req.getCompanyContacts());
        record.setContactsPhone(req.getContactsPhone());
        record.setPracticeStatus(PracticeStatus.NOT_BEGIN.getValue());
//        record.setAttendanceState(PracticeAttendanceState.RUNNING.getValue());
        record.setAttendanceState(PracticeAttendanceState.PASS.getValue());
        record.setStatus(ApprovalStatus.UN_REPORT.getValue());
        // 所有记录的层级保存到校级
        record.setOrgIndexPath(school.getIndexPath() + school.getOrgIndex());
        record.setOrgPathName(school.getOrgName() + "<" + school.getIndexPathName());
        record.setCreatorCode(user.getUserCode());
        record.setCreatorName(user.getUserName());
        record.setCreatorRoleName(AdminRole.HEAD_TEACHER.getName());
        record.setCreatedAt(LocalDateTime.now());
        practiceRecordMapper.insertUseGeneratedKeys(record);

        // 保存实习人员名单
        savePracticePersons(record.getId(), getPersonNos(req.getPersonNos()));

        // 保存附件文件
        saveAttachFiles(record.getId(), RecordType.PRACTICE, req.getFiles());

        // 是否同时上报
        if (req.isReport()) {
            reportRecords(Collections.singletonList(record), user);
        }

        log.info("[实习管理] - {} 提交了实习申请：{}", user.getUserName(), record.getId());
        return record.getId();
    }

    /**
     * 保存实习人员
     *
     * @param recordId 实习审批记录ID
     * @param persons  人员列表
     */
    private void savePracticePersons(Integer recordId, List<PersonOfClassBO> persons) {
        practicePersonMapper.insertList(persons
                .stream()
                .map(src -> {
                    PracticePersonDO dst = new PracticePersonDO();
                    dst.setPracticeRecordId(recordId);
                    dst.setPersonNo(src.getPersonNo());
                    dst.setPersonName(src.getPersonName());
                    dst.setOrgIndex(src.getOrgIndex());
                    dst.setOrgName(src.getOrgName());
                    return dst;
                })
                .collect(Collectors.toList()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Integer id, PracticeAddReqDTO req) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        // 查找要更新的记录
        PracticeRecordDO record = practiceRecordMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new InvalidParameterException("记录不存在");
        }

        // 只有创建人本人才可修改
        if (!record.getCreatorCode().equals(user.getUserCode())) {
            throw new InvalidParameterException("非当前用户所创建，无法编辑");
        }

        // 只有未上报的记录可更新
        if (ApprovalStatus.UN_REPORT.getValue() != record.getStatus()) {
            throw new InvalidParameterException("申请已上报，无法更新");
        }

        // 校验时间参数
        LocalDate startDate = req.getStartDate();
        LocalDate endDate = req.getEndDate();
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("实习开始日期不能大于结束日期");
        }

        // 更新参数
        record.setStartDate(startDate);
        record.setEndDate(endDate);
        record.setPracticeCompany(req.getPracticeCompany());
        record.setCompanyContacts(req.getCompanyContacts());
        record.setContactsPhone(req.getContactsPhone());
        record.setUpdatedAt(LocalDateTime.now());
        practiceRecordMapper.updateByPrimaryKeySelective(record);

        // 删除之前所有的人员名单
        practicePersonMapper.deleteByCondition(Example.builder(PracticePersonDO.class)
                .where(Sqls.custom()
                        .andEqualTo("practiceRecordId", record.getId()))
                .build());

        // 重新保存实习人员名单
        savePracticePersons(record.getId(), getPersonNos(req.getPersonNos()));

        // 删除之前所有关联的文件
        deleteAttachFiles(record.getId(), RecordType.PRACTICE);

        // 保存新的附件文件
        saveAttachFiles(record.getId(), RecordType.PRACTICE, req.getFiles());

        // 是否同时上报
        if (req.isReport()) {
            reportRecords(Collections.singletonList(record), user);
        }

        log.info("[实习管理] - {} 更新了实习申请：{}", user.getUserName(), record.getId());
    }

    @Override
    public PracticeRecordDetailDTO get(Integer id) {
        // 根据主键查询记录
        PracticeRecordDetailDTO record = practiceRecordMapper.findById(id);
        if (record == null) {
            throw new InvalidParameterException("记录不存在");
        }

        // 查询关联的人员列表
        record.setPersons(practicePersonMapper.findPersonsByRecordId(record.getId()));

        // 查询流程列表
        record.setProcesses(getProcesses(id, RecordType.PRACTICE.getValue()));

        // 查询关联的文件列表
        record.setFiles(getAttachFiles(record.getId(), RecordType.PRACTICE));
        return record;
    }

    @Override
    public PaginationRes<PracticeRecordListDTO> list(PracticeRecordQueryReqDTO req) {
        setUserIndexPath(req);

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PracticeRecordListDTO> records = practiceRecordMapper.findByConditions(req);
        if (records.isEmpty()) {
            return PaginationRes.of(records, page);
        }

        List<Integer> recordIds = records
                .stream()
                .map(PracticeRecordListDTO::getId)
                .distinct()
                .collect(Collectors.toList());

        // 补充人员姓名参数，当需要对人员姓名检索时，使用条件查询使用string_agg只能查询出一个人员，所以这里重新查询人员姓名列表
        Map<Integer, String> personNamesMap = practicePersonMapper.findPersonNameByRecordIds(recordIds)
                .stream()
                .collect(Collectors.toMap(DictBO::getId, DictBO::getValue));
        for (PracticeRecordListDTO record : records) {
            record.setPersonNames(personNamesMap.get(record.getId()));
        }

        // H5端列表需要展示第一个人员的图片url
        if (req.isFromMobile()) {
            Map<Integer, String> personUrlMap = practicePersonMapper.findFirstPersonUrl(recordIds)
                    .stream()
                    .collect(Collectors.toMap(DictBO::getId, DictBO::getValue));
            for (PracticeRecordListDTO record : records) {
                record.setFirstPersonUrl(personUrlMap.get(record.getId()));
            }
        }
        return PaginationRes.of(records, page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void report(List<Integer> ids) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        // 查询实习记录列表
        Example example = Example.builder(PracticeRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", ids))
                .build();
        List<PracticeRecordDO> records = practiceRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("记录不存在");
        }

        for (PracticeRecordDO record : records) {
            // 判断记录是否已上报
            if (record.getStatus() != ApprovalStatus.UN_REPORT.getValue()) {
                throw new InvalidParameterException("包含已上报的记录: " + record.getId());
            }
        }

        reportRecords(records, user);
    }

    /**
     * 批量上报记录
     *
     * @param records 实习申请记录列表
     * @param user    当前用户
     */
    private void reportRecords(List<PracticeRecordDO> records, UserStruct user) {
        for (PracticeRecordDO record : records) {
            // 保存审批流程
            List<ApproveRecordDO> approveRecords = new ArrayList<>(2);
            approveRecords.add(ApproveRecordDO.builder()
                    .serialNo(1)
                    .targetId(record.getId())
                    .targetType(RecordType.PRACTICE.getValue())
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
                    .targetType(RecordType.PRACTICE.getValue())
                    .name("学校审批")
                    .assigneeRoleCode(AdminRole.SCHOOL.getCode())
                    .assigneeRoleName(AdminRole.SCHOOL.getName())
                    .build());
            approveRecordMapper.batchSave(approveRecords);

            record.setStatus(ApprovalStatus.UN_PROCESS.getValue());
            record.setReportedAt(LocalDateTime.now());
            practiceRecordMapper.updateByPrimaryKeySelective(record);
            log.info("[实习管理] - {} 上报了记录: {}", user.getUserName(), record.getId());
        }
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

        // 查询实习申请列表
        Example example = Example.builder(PracticeRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", req.getIds()))
                .build();
        List<PracticeRecordDO> records = practiceRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("记录不存在");
        }

        for (PracticeRecordDO record : records) {
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
                            .andEqualTo("targetType", RecordType.PRACTICE.getValue())
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

                // 审批通过，修改考勤的状态
                List<String> personNos = practicePersonMapper.findPersonNosOfRecordId(record.getId());
                for (String personNo : personNos) {
                    // 调整日常考勤
                    dailyAttendanceResultService.updateResults(personNo, record.getStartDate(), record.getEndDate(),
                            AttendanceResult.PRACTICE, req.getComment(), AdjustModeEnum.SYSTEM_CHANGE);
                    // 调整抽查考勤
                    taskResultService.updateResults(personNo, record.getStartDate(), record.getEndDate(),
                            AttendanceResult.PRACTICE, req.getComment(), AdjustModeEnum.SYSTEM_CHANGE);
                }
            } else {
                // 拒绝
                record.setStatus(ApprovalStatus.REJECTED.getValue());
            }

            // 计算实习状态
            record.setPracticeStatus(calcPracticeStatus(record).getValue());

            practiceRecordMapper.updateByPrimaryKeySelective(record);
        }

        log.info("{} 审批了实习申请 {}，审批结果: {}", user.getUserCode(), req.getIds(), req.getResult());
    }

    /**
     * 根据实习的时间计算实习状态
     *
     * @param record 实习记录
     * @return 实习状态
     */
    private PracticeStatus calcPracticeStatus(PracticeRecordDO record) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = record.getStartDate();
        LocalDate endDate = record.getEndDate();
        if (now.isBefore(startDate)) {
            // 开始日期小于当天开始日期，待实习
            return PracticeStatus.NOT_BEGIN;
        }

        if (now.isAfter(endDate)) {
            // 结束日期小于当天开始日期，实习结束
            return PracticeStatus.FINISHED;
        }
        // 正在实习
        return PracticeStatus.PRACTICING;
    }

    @Override
    public void delete(List<Integer> ids) {
        User user = UserUtils.getUser();
        Example example = Example.builder(PracticeRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", ids))
                .build();

        List<PracticeRecordDO> records = practiceRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("实习申请不存在");
        }

        for (PracticeRecordDO record : records) {
            if (record.getStatus() != ApprovalStatus.UN_REPORT.getValue()) {
                throw new InvalidParameterException("只有未上报的记录可删除");
            }
            if (!record.getCreatorCode().equals(user.getUserCode())) {
                throw new InvalidParameterException("不能删除非本人提交的记录");
            }
        }

        practiceRecordMapper.deleteByCondition(example);
        // 删除附件文件
        batchDeleteAttachFiles(ids, RecordType.PRACTICE);

        log.info("[实习管理] - {} 删除了实习审批记录: {}", user.getUserName(), ids);
    }

    @Override
    public PracticeRecordDetailDTO selectDetailByPersonNo(String personNo, LocalDate date) {
        Integer id = practicePersonMapper.selectByPersonNoAndDate(personNo, date);
        return get(id);
    }
}
