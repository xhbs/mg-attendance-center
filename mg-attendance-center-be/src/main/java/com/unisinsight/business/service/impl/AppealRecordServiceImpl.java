package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.common.enums.*;
import com.unisinsight.business.common.utils.UserStruct;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.common.utils.excel.Column;
import com.unisinsight.business.common.utils.excel.ExcelExportHelper;
import com.unisinsight.business.dto.request.AppealAddReqDTO;
import com.unisinsight.business.dto.request.AppealRecordQueryReqDTO;
import com.unisinsight.business.dto.request.ApprovalReqDTO;
import com.unisinsight.business.dto.response.AppealRecordDetailDTO;
import com.unisinsight.business.dto.response.AppealRecordListDTO;
import com.unisinsight.business.dto.response.PersonBaseInfoDTO;
import com.unisinsight.business.manager.FMSManager;
import com.unisinsight.business.mapper.AppealPersonMapper;
import com.unisinsight.business.mapper.AppealRecordMapper;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.model.*;
import com.unisinsight.business.rpc.InfraClient;
import com.unisinsight.business.rpc.dto.UserDetailResDTO;
import com.unisinsight.business.service.AppealRecordService;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.bean.BeanCopyUtils;
import com.unisinsight.framework.common.util.date.DateUtils;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考勤申诉记录服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11 11:23:53
 * @since 1.0
 */
@Slf4j
@Service
public class AppealRecordServiceImpl extends ApprovalRecordService implements AppealRecordService {

    @Resource
    private AppealRecordMapper appealRecordMapper;

    @Resource
    private AppealPersonMapper appealPersonMapper;

    @Resource
    private OrganizationMapper organizationMapper;

    @Resource
    private InfraClient infraClient;

    @Resource
    private FMSManager fmsManager;

    /**
     * 学号所在列
     */
    @Value("${attendance-result-export.template.personno-column}")
    private String personNoColumn;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(AppealAddReqDTO req) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        HashSet<String> roleSet = user.getRoleSet();
        if (roleSet == null) {
            throw new InvalidParameterException("未分配角色");
        }

        if (!roleSet.contains(AdminRole.SCHOOL.getCode())) {
            throw new InvalidParameterException("不是校级资助管理员，无法发起申诉流程");
        }

        // 校验：申诉标题不能重复
        Example example = Example.builder(AppealRecordDO.class)
                .where(Sqls.custom()
                        .andEqualTo("title", req.getTitle()))
                .build();
        int count = appealRecordMapper.selectCountByCondition(example);
        if (count > 0) {
            throw new InvalidParameterException("申诉标题已存在");
        }

        // 校验时间参数
        LocalDate startDate = DateUtils.fromMilliseconds(req.getStartTime()).toLocalDate();
        LocalDate endDate = DateUtils.fromMilliseconds(req.getEndTime()).toLocalDate();
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("考勤开始日期不能大于结束日期");
        }

        // 校验申诉人员名单
        List<String> personNos = req.getPersonNos() == null ? new ArrayList<>() : req.getPersonNos();
        if (req.getNameListExcelPath() != null) {
            personNos.addAll(getPersonNosFromExcel(req.getNameListExcelPath()));
        }
        if (personNos.isEmpty()) {
            throw new InvalidParameterException("申诉学生名单为空");
        }

        // 根据当前用户的org_index查找所属学校
        OrganizationDO school = user.getSchool();
        if (school == null) {
            throw new BaseException("当前用户不属于任何学校");
        }
        OrganizationDO superior = user.getSuperiorOfSchool();
        if (superior == null) {
            throw new InvalidParameterException("所属学校上级组织不存在");
        }

        // 保存申诉记录
        AppealRecordDO record = new AppealRecordDO();
        record.setTitle(req.getTitle());
        record.setContent(req.getContent());
        record.setStartDate(startDate);
        record.setEndDate(endDate);
        record.setSchoolParentSubType(superior.getSubType());
        record.setStatus(ApprovalStatus.UN_REPORT.getValue());
        // 所有记录的层级保存到校级
        record.setOrgIndexPath(school.getIndexPath() + school.getOrgIndex());
        record.setOrgPathName(school.getOrgName() + "<" + school.getIndexPathName());
        record.setCreatorCode(user.getUserCode());
        record.setCreatorName(user.getUserName());
        record.setCreatorRoleName(AdminRole.SCHOOL.getName());
        record.setCreatedAt(LocalDateTime.now());
        appealRecordMapper.insertUseGeneratedKeys(record);

        // 保存附件文件
        saveAttachFiles(record.getId(), RecordType.APPEAL, req.getFiles());

        // 保存申诉人员名单
        saveAppealPersons(record.getId(), getPersonNos(personNos));

        // 是否同时申诉
        if (req.isReport()) {
            reportRecords(Collections.singletonList(record), user);
        }

        log.info("[申诉管理] - {} 提交了申诉记录：{}", user.getUserName(), record.getTitle());
    }

    /**
     * 从申诉名单excel中读取人员名单
     */
    private List<String> getPersonNosFromExcel(String excelPath) {
        String excelUrl = fmsManager.getAbsoluteUrl(excelPath);
        log.info("申诉名单excel： {}", excelUrl);

        HttpURLConnection conn = null;
        InputStream is = null;

        try {
            conn = (HttpURLConnection) new URL(excelUrl).openConnection();
            is = conn.getInputStream();

            Workbook wb;
            if (excelUrl.endsWith("xls")) {
                wb = new HSSFWorkbook(is);
            } else {
                wb = new XSSFWorkbook(is);
            }

            Sheet sheet = wb.getSheetAt(0); // 读取第一张表
            int lastRowIndex = sheet.getLastRowNum();// 最后一行行号
            int lastColumnIndex = -1;// 最后一列编号

            int personInfoStartRowIndex = -1;// 学生信息开始行
            int idNumberColumnIndex = -1;// 身份证号所在列

            for (int i = 0; i < lastRowIndex; i++) {
                Row row = sheet.getRow(i);
                lastColumnIndex = row.getLastCellNum();

                for (int j = 0; j < lastColumnIndex; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        continue;
                    }

                    cell.setCellType(CellType.STRING);
                    if (personNoColumn.equals(cell.getStringCellValue())) {
                        // 身份证号所在列
                        idNumberColumnIndex = j;
                        personInfoStartRowIndex = i;
                        break;
                    }
                }
            }

            int personCount = lastRowIndex - personInfoStartRowIndex + 1;
            log.info("学生信息开始行: {}, 身份证号所在列: {}； 一共 {} 个学生，一共 {} 列", personInfoStartRowIndex,
                    idNumberColumnIndex, personCount, lastColumnIndex);

            // 读取所有身份证件号
            List<String> personNos = new ArrayList<>(personCount);
            for (int i = personInfoStartRowIndex; i <= lastRowIndex; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                // 读取身份证件号
                Cell cell = row.getCell(idNumberColumnIndex);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    String personNo = cell.getStringCellValue();
                    if (StringUtils.isNotEmpty(personNo)) {
                        personNos.add(personNo);
                    }
                }
            }

            log.info("读取到excel中 {} 个学生", personNos.size());
            return personNos;
        } catch (IOException e) {
            log.error("", e);
            throw new InvalidParameterException("读取excel失败");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 保存申诉人员
     *
     * @param recordId 申诉记录ID
     * @param persons  申诉人员列表
     */
    private void saveAppealPersons(Integer recordId, List<PersonOfClassBO> persons) {
        appealPersonMapper.insertList(persons
                .stream()
                .map(src -> {
                    AppealPersonDO dst = new AppealPersonDO();
                    dst.setAppealRecordId(recordId);
                    dst.setPersonNo(src.getPersonNo());
                    dst.setPersonName(src.getPersonName());
                    return dst;
                })
                .collect(Collectors.toList()));
    }

    /**
     * 删除关联的申诉名单
     *
     * @param record 申诉记录
     */
    private void deleteAppealPersons(AppealRecordDO record) {
        appealPersonMapper.deleteByCondition(Example.builder(AppealPersonDO.class)
                .where(Sqls.custom()
                        .andEqualTo("appealRecordId", record.getId()))
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Integer id, AppealAddReqDTO req) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        // 查找要更新的记录
        AppealRecordDO record = appealRecordMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new InvalidParameterException("记录不存在");
        }

        // 只有创建人本人才可修改
        if (!record.getCreatorCode().equals(user.getUserCode())) {
            throw new InvalidParameterException("非当前用户所创建，无法编辑");
        }

        // 只有未上报的记录可更新
        if (ApprovalStatus.UN_REPORT.getValue() != record.getStatus()) {
            throw new InvalidParameterException("申诉已上报，无法更新");
        }

        // 是否更新标题
        if (!record.getTitle().equals(req.getTitle())) {
            // 校验：申诉标题不能重复
            Example example = Example.builder(AppealRecordDO.class)
                    .where(Sqls.custom()
                            .andEqualTo("title", req.getTitle()))
                    .build();
            int count = appealRecordMapper.selectCountByCondition(example);
            if (count > 0) {
                throw new InvalidParameterException("申诉标题已存在");
            }

            record.setTitle(req.getTitle());
        }

        // 校验时间参数
        LocalDate startDate = DateUtils.fromMilliseconds(req.getStartTime()).toLocalDate();
        LocalDate endDate = DateUtils.fromMilliseconds(req.getEndTime()).toLocalDate();
        if (startDate.isAfter(endDate)) {
            throw new InvalidParameterException("考勤开始日期不能大于结束日期");
        }

        // 校验申诉人员名单
        List<String> personNos = req.getPersonNos() == null ? new ArrayList<>() : req.getPersonNos();
        if (req.getNameListExcelPath() != null) {
            personNos.addAll(getPersonNosFromExcel(req.getNameListExcelPath()));
        }
        if (personNos.isEmpty()) {
            throw new InvalidParameterException("申诉学生名单为空");
        }

        // 更新申诉参数
        record.setContent(req.getContent());
        record.setStartDate(startDate);
        record.setEndDate(endDate);
        record.setUpdatedAt(LocalDateTime.now());
        appealRecordMapper.updateByPrimaryKeySelective(record);
        log.info("[申诉管理] - {} 更新了申诉记录：{}", user.getUserName(), record.getTitle());

        // 删除之前所有关联的文件
        deleteAttachFiles(record.getId(), RecordType.APPEAL);

        // 保存新的附件文件
        saveAttachFiles(record.getId(), RecordType.APPEAL, req.getFiles());

        // 删除之前所有的申诉人员
        deleteAppealPersons(record);

        // 保存申诉人员名单
        saveAppealPersons(record.getId(), getPersonNos(req.getPersonNos()));

        // 是否同时申诉
        if (req.isReport()) {
            reportRecords(Collections.singletonList(record), user);
        }
    }

    /**
     * 批量上报记录, 校管理员申请，上级资助管理员审批
     *
     * @param records 申诉记录列表
     * @param user    当前用户
     */
    private void reportRecords(List<AppealRecordDO> records, UserStruct user) {
        // 根据学校的上级组织来判断走什么流程
        OrganizationDO school = user.getSchool();
        if (school == null) {
            throw new InvalidParameterException("当前用户不属于任何学校");
        }
        OrganizationDO superiorOfSchool = organizationMapper.selectByOrgIndex(school.getOrgParentIndex());
        if (superiorOfSchool == null) {
            throw new InvalidParameterException("未找到学校所属组织");
        }

        for (AppealRecordDO record : records) {
            // 保存审批流程
            List<ApproveRecordDO> approveRecords = new ArrayList<>(2);
            approveRecords.add(ApproveRecordDO.builder()
                    .serialNo(1)
                    .targetId(record.getId())
                    .targetType(RecordType.APPEAL.getValue())
                    .name("学校申诉")
                    .result(ApprovalResult.PASS.getValue())
                    .assigneeUserCode(user.getUserCode())
                    .assigneeUserName(user.getUserName())
                    .assigneeRoleCode(AdminRole.SCHOOL.getCode())
                    .assigneeRoleName(AdminRole.SCHOOL.getName())
                    .assignedAt(LocalDateTime.now())
                    .build());
            if (superiorOfSchool.getSubType() == OrgType.PROVINCE.getValue()) {
                // 省直属学校
                approveRecords.add(ApproveRecordDO.builder()
                        .serialNo(2)
                        .targetId(record.getId())
                        .targetType(RecordType.APPEAL.getValue())
                        .name("省级审批")
                        .assigneeRoleCode(AdminRole.PROVINCE.getCode())
                        .assigneeRoleName(AdminRole.PROVINCE.getName())
                        .build());
            } else if (superiorOfSchool.getSubType() == OrgType.CITY.getValue()) {
                // 市直属学校
                approveRecords.add(ApproveRecordDO.builder()
                        .serialNo(2)
                        .targetId(record.getId())
                        .targetType(RecordType.APPEAL.getValue())
                        .name("市级审批")
                        .assigneeRoleCode(AdminRole.CITY.getCode())
                        .assigneeRoleName(AdminRole.CITY.getName())
                        .build());
            } else {
                approveRecords.add(ApproveRecordDO.builder()
                        .serialNo(2)
                        .targetId(record.getId())
                        .targetType(RecordType.APPEAL.getValue())
                        .name("区县审批")
                        .assigneeRoleCode(AdminRole.COUNTY.getCode())
                        .assigneeRoleName(AdminRole.COUNTY.getName())
                        .build());
            }
            approveRecordMapper.batchSave(approveRecords);

            record.setStatus(ApprovalStatus.UN_PROCESS.getValue());
            record.setReportedAt(LocalDateTime.now());
            appealRecordMapper.updateByPrimaryKeySelective(record);
            log.info("[申诉管理] - {} 上报了记录: {}", user.getUserName(), record.getId());
        }
    }

    @Override
    public AppealRecordDetailDTO get(Integer id) {
        // 根据主键查询记录
        AppealRecordDO record = appealRecordMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new InvalidParameterException("记录不存在");
        }

        AppealRecordDetailDTO res = BeanCopyUtils.convert(record, AppealRecordDetailDTO.class);

        // 查询关联的文件列表
        res.setFiles(getAttachFiles(record.getId(), RecordType.APPEAL));

        // 查询关联的人员列表
        List<AppealPersonDO> persons = appealPersonMapper.selectByCondition(Example.builder(AppealPersonDO.class)
                .where(Sqls.custom()
                        .andEqualTo("appealRecordId", record.getId()))
                .build());
        if (!persons.isEmpty()) {
            res.setPersons(BeanCopyUtils.convertList(persons, PersonBaseInfoDTO.class));
        }

        // 查询流程列表
        res.setProcesses(getProcesses(id, RecordType.APPEAL.getValue()));
        return res;
    }

    @Override
    public PaginationRes<AppealRecordListDTO> list(AppealRecordQueryReqDTO req) {
        setPermissionCondition(req);

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<AppealRecordListDTO> records = appealRecordMapper.findByConditions(req);
        return PaginationRes.of(records, page);
    }

    /**
     * 根据当前用户的角色、组织设置权限查询条件
     */
    private void setPermissionCondition(AppealRecordQueryReqDTO req) {
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);
        req.setUserCode(user.getUserCode());

        AdminRole adminRole = user.getAdminRoleCode();
        if (adminRole == null) {
            throw new InvalidParameterException("没有查询权限");
        }
        req.setAdminLevel(adminRole.getLevel());

        // 当前用户所属组织
        UserDetailResDTO userDetail = user.getUserDetail();
        OrganizationDO org = organizationMapper.selectByPrimaryKey(userDetail.getOrgId());
        if (org == null) {
            throw new InvalidParameterException("未找到当前用户所属组织");
        }

        req.setAdminIndexPath(org.getIndexPath() + userDetail.getOrgIndex());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void appeal(List<Integer> ids) {
        // 判断用户是否登录，获取当前用户信息
        UserStruct user = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);

        // 查询申诉记录列表
        Example example = Example.builder(AppealRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", ids))
                .build();
        List<AppealRecordDO> records = appealRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("记录不存在");
        }

        for (AppealRecordDO record : records) {
            // 判断记录是否已上报
            if (record.getStatus() != ApprovalStatus.UN_REPORT.getValue()) {
                throw new InvalidParameterException("包含已上报的记录: " + record.getId());
            }
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
            throw new InvalidParameterException("用户未分配角色，无权审批");
        }

        String userIndexPath = user.getIndexPath();
        if (userIndexPath == null) {
            throw new InvalidParameterException("用户未关联到组织");
        }

        // 查询审批记录
        Example example = Example.builder(LeaveRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", req.getIds())
                )
                .build();
        List<AppealRecordDO> records = appealRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("记录不存在");
        }

        for (AppealRecordDO record : records) {
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
                            .andEqualTo("targetType", RecordType.APPEAL.getValue())
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

                // 审批通过，调整考勤
                List<String> personNos = appealPersonMapper.findPersonNosOfRecordId(record.getId());
                for (String personNo : personNos) {
                    // 审批通过，调整日常考勤
                    dailyAttendanceResultService.updateResults(personNo, record.getStartDate(), record.getEndDate(),
                            AttendanceResult.NORMAL, req.getComment(), AdjustModeEnum.ALLEGEDLY_CHANGE);
                    // 调整抽查考勤
                    taskResultService.updateResults(personNo, record.getStartDate(), record.getEndDate(),
                            AttendanceResult.APPEAL, req.getComment(), AdjustModeEnum.ALLEGEDLY_CHANGE);
                }
            } else {
                // 拒绝
                record.setStatus(ApprovalStatus.REJECTED.getValue());
            }
            appealRecordMapper.updateByPrimaryKeySelective(record);
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        User user = UserUtils.getUser();
        Example example = Example.builder(AppealRecordDO.class)
                .where(Sqls.custom()
                        .andIn("id", ids))
                .build();

        List<AppealRecordDO> records = appealRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            throw new InvalidParameterException("申诉记录不存在");
        }

        for (AppealRecordDO record : records) {
            if (record.getStatus() != ApprovalStatus.UN_REPORT.getValue()) {
                throw new InvalidParameterException("只有未上报的记录可删除");
            }
            if (!record.getCreatorCode().equals(user.getUserCode())) {
                throw new InvalidParameterException("不能删除非本人提交的记录");
            }
        }

        appealRecordMapper.deleteByCondition(example);
        // 删除附件文件
        batchDeleteAttachFiles(ids, RecordType.APPEAL);

        log.info("[申诉管理] - {} 删除了申诉记录: {}", user.getUserName(), ids);
    }

    @Override
    public void export(AppealRecordQueryReqDTO req, HttpServletResponse resp) {
        setPermissionCondition(req);

        int total = appealRecordMapper.countByConditions(req);
        if (total == 0) {
            throw new InvalidParameterException("导出记录为空");
        }

        // 限制最大导出数量
        total = Math.min(total, excelMaxExportRows);
        req.setLimit(excelMaxExportRows);

        // 配置excel的head信息和字段转换
        List<Column<AppealRecordListDTO>> columns = new ArrayList<>();
        columns.add(new Column<>("序号", 4000, (index, value) -> index));

        // 处理状态
        columns.add(new Column<>("处理状态", 4000, (index, value) ->
                ApprovalStatus.getNameOfValue(value.getStatus())));

        columns.add(new Column<>("申诉标题", 8000, (index, value) -> value.getTitle()));
        columns.add(new Column<>("申诉内容", 8000, (index, value) -> value.getContent()));
        columns.add(new Column<>("考勤时段", 8000, (index, value) -> {
            if (value.getEndDate().isEqual(value.getStartDate())) {
                // 同一天时间显示优化
                return DateUtils.DATE_FORMATTER.format(value.getStartDate());
            }
            return DateUtils.DATE_FORMATTER.format(value.getStartDate()) + " - " +
                    DateUtils.DATE_FORMATTER.format(value.getEndDate());
        }));
        columns.add(new Column<>("申诉组织", 8000, (index, value) -> value.getOrgPathName()));
        columns.add(new Column<>("申诉时间", 6000, (index, value) -> {
            if (value.getReportedAt() == null) {
                return null;
            }
            return DateUtils.DATETIME_FORMATTER.format(value.getReportedAt());
        }));

        try {
            ExcelExportHelper<AppealRecordListDTO> handler = new ExcelExportHelper<>(columns, total,
                    resp.getOutputStream());
            handler.setProgressListener(progress -> log.info("抽查申诉导出进度：" + progress));
            appealRecordMapper.exportByConditions(req, handler);
            handler.done();
        } catch (IOException e) {
            log.error("", e);
            throw new BaseException("导出失败");
        }
    }
}
