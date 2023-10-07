package com.unisinsight.business.service.impl;

import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.common.enums.RecordType;
import com.unisinsight.business.common.utils.UserStruct;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.request.ApprovalRecordQueryReqDTO;
import com.unisinsight.business.dto.response.AttachFileDTO;
import com.unisinsight.business.dto.response.ProcessDTO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.ApproveRecordDO;
import com.unisinsight.business.model.AttachFileDO;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.rpc.InfraClient;
import com.unisinsight.business.rpc.dto.UserDetailResDTO;
import com.unisinsight.business.service.DailyAttendanceResultService;
import com.unisinsight.business.service.TaskResultService;
import com.unisinsight.framework.common.util.bean.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 请假、实习、申诉流程的公共逻辑封装
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/21
 * @since 1.0
 */
@Slf4j
public abstract class ApprovalRecordService {

    @Value("${excel-export.max-size:100000}")
    protected int excelMaxExportRows;

    @Resource
    protected OrganizationMapper organizationMapper;

    @Resource
    protected PersonMapper personMapper;

    @Resource
    protected PracticeRecordMapper practiceRecordMapper;

    @Resource
    protected LeaveRecordMapper leaveRecordMapper;

    @Resource
    protected InfraClient infraClient;

    @Resource
    protected DailyAttendanceResultService dailyAttendanceResultService;

    @Resource
    protected TaskResultService taskResultService;

    @Resource
    protected AttachFileMapper attachFileMapper;

    @Resource
    protected ApproveRecordMapper approveRecordMapper;

    /**
     * 添加实习、请假时候，检查同一个人在同一个时间段内是否有已存在的实习和请假记录
     *
     * @param personNos 人员编号列表
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    void checkRepeat(List<String> personNos, LocalDate startDate, LocalDate endDate) {
        // 查找重复的实习记录
        List<String> practiceRepeats = practiceRecordMapper.checkRepeat(personNos, startDate, endDate);
        // 查找重复的请假记录
        List<String> leaveRepeats = leaveRecordMapper.checkRepeat(personNos, startDate, endDate);

        StringBuilder sb = new StringBuilder();
        if (!practiceRepeats.isEmpty()) {
            sb.append("已存在实习申请：")
                    .append(String.join("、", practiceRepeats))
                    .append("; ");
        }

        if (!leaveRepeats.isEmpty()) {
            sb.append("已存在请假记录：")
                    .append(String.join("、", leaveRepeats));
        }

        // 错误提示返回给前端
        String error = sb.toString();
        if (StringUtils.isNotEmpty(error)) {
            throw new InvalidParameterException(error);
        }
    }

    /**
     * 根据人员编号列表查询人员列表
     */
    List<PersonOfClassBO> getPersonNos(List<String> personNos) {
        List<PersonOfClassBO> persons = personMapper.findPersonsOfClass(personNos);
        if (persons.isEmpty()) {
            throw new InvalidParameterException("学生不存在");
        }
        return persons;
    }

    /**
     * 查询用户所属组织信息
     */
    void setUserIndexPath(ApprovalRecordQueryReqDTO req) {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);
        req.setUserCode(user.getUserCode());

        // 当前用户所属组织
        UserDetailResDTO userDetail = user.getUserDetail();
        OrganizationDO org = organizationMapper.selectByPrimaryKey(userDetail.getOrgId());
        if (org == null) {
            throw new InvalidParameterException("未找到当前用户所属组织");
        }
        req.setAdminIndexPath(org.getIndexPath() + userDetail.getOrgIndex());
    }

    /**
     * 保存附件文件
     *
     * @param recordId   记录ID
     * @param recordType 记录类型
     * @param files      文件列表
     */
    void saveAttachFiles(Integer recordId, RecordType recordType, List<AttachFileDTO> files) {
        if (CollectionUtils.isNotEmpty(files)) {
            attachFileMapper.batchSave(files.stream()
                    .map(file -> {
                        AttachFileDO dst = new AttachFileDO();
                        dst.setRecordId(recordId);
                        dst.setRecordType(recordType.getValue());
                        dst.setFileName(file.getFileName());
                        dst.setFilePath(file.getFilePath());
                        return dst;
                    }).collect(Collectors.toList()));
            log.info("[附件文件] - 保存 {} 个附件文件", files.size());
        }
    }

    /**
     * 查询附件文件
     *
     * @param recordId   记录ID
     * @param recordType 记录类型
     * @return 附件集合
     */
    List<AttachFileDTO> getAttachFiles(Integer recordId, RecordType recordType) {
        // 查询关联的文件列表
        List<AttachFileDO> files = attachFileMapper.selectByCondition(Example.builder(AttachFileDO.class)
                .where(Sqls.custom()
                        .andEqualTo("recordId", recordId)
                        .andEqualTo("recordType", recordType.getValue())
                )
                .build());
        if (!files.isEmpty()) {
            return BeanCopyUtils.convertList(files, AttachFileDTO.class);
        }
        return null;
    }

    /**
     * 删除附件文件
     *
     * @param recordId   记录ID
     * @param recordType 记录类型
     */
    void deleteAttachFiles(Integer recordId, RecordType recordType) {
        try {
            attachFileMapper.deleteByCondition(Example.builder(AttachFileDO.class)
                    .where(Sqls.custom()
                            .andEqualTo("recordId", recordId)
                            .andEqualTo("recordType", recordType.getValue())
                    )
                    .build());
        } catch (Exception e) {
            log.error("删除附件文件失败", e);
        }
    }

    /**
     * 批量删除附件文件
     *
     * @param recordIds  记录ID集合
     * @param recordType 记录类型
     */
    void batchDeleteAttachFiles(List<Integer> recordIds, RecordType recordType) {
        try {
            attachFileMapper.deleteByCondition(Example.builder(AttachFileDO.class)
                    .where(Sqls.custom()
                            .andEqualTo("recordType", recordType.getValue())
                            .andIn("recordId", recordIds)
                    )
                    .build());
        } catch (Exception e) {
            log.error("删除附件文件失败", e);
        }
    }

    /**
     * 查询审批流程
     *
     * @param targetId   记录ID
     * @param targetType 记录类型
     * @return 审批流程列表
     */
    List<ProcessDTO> getProcesses(Integer targetId, Integer targetType) {
        // 查询流程列表
        Example example = Example.builder(ApproveRecordDO.class)
                .where(Sqls.custom()
                        .andEqualTo("targetId", targetId)
                        .andEqualTo("targetType", targetType)
                )
                .orderByAsc("serialNo")
                .build();
        List<ApproveRecordDO> approveRecords = approveRecordMapper.selectByCondition(example);
        return approveRecords.isEmpty() ? null : approveRecords
                .stream()
                .map(src -> {
                    ProcessDTO dst = new ProcessDTO();
                    dst.setId(src.getId());
                    dst.setSerialNo(src.getSerialNo());
                    dst.setName(src.getName());
                    dst.setCompletedAt(src.getAssignedAt());
                    dst.setResult(src.getResult());
                    dst.setComment(src.getComment());
                    dst.setAssigneeUserCode(src.getAssigneeUserCode());
                    dst.setAssigneeUserName(src.getAssigneeUserName());
                    dst.setAssigneeRoleCode(src.getAssigneeRoleCode());
                    dst.setAssigneeRoleName(src.getAssigneeRoleName());
                    dst.setCompleted(src.getAssignedAt() != null);// 兼容字段
                    dst.setCurrent(src.getAssignedAt() == null);
                    return dst;
                }).collect(Collectors.toList());
    }
}
