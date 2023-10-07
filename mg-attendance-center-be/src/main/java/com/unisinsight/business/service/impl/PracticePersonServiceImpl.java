package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.enums.PracticeStatus;
import com.unisinsight.business.common.enums.RecordType;
import com.unisinsight.business.common.utils.excel.Column;
import com.unisinsight.business.common.utils.excel.ExcelExportHelper;
import com.unisinsight.business.dto.request.PracticePersonQueryReqDTO;
import com.unisinsight.business.dto.response.PracticePersonDetailDTO;
import com.unisinsight.business.dto.response.PracticePersonListDTO;
import com.unisinsight.business.mapper.PracticePersonMapper;
import com.unisinsight.business.service.PracticePersonService;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实习人员统计服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
@Slf4j
@Service
public class PracticePersonServiceImpl extends ApprovalRecordService implements PracticePersonService {

    @Resource
    private PracticePersonMapper practicePersonMapper;

    @Override
    public PracticePersonDetailDTO get(Integer id) {
        // 查找记录
        PracticePersonDetailDTO res = practicePersonMapper.findById(id);
        if (res == null) {
            throw new InvalidParameterException("记录不存在");
        }

        // 查找流程记录
        res.setProcesses(getProcesses(res.getPracticeRecordId(), RecordType.PRACTICE.getValue()));

        // 查询关联的文件列表
        res.setFiles(getAttachFiles(res.getPracticeRecordId(), RecordType.PRACTICE));
        return res;
    }

    @Override
    public PaginationRes<PracticePersonListDTO> list(PracticePersonQueryReqDTO req) {
        setUserIndexPath(req);

        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PracticePersonListDTO> records = practicePersonMapper.findByConditions(req);
        return PaginationRes.of(records, page);
    }

    @Override
    public void export(PracticePersonQueryReqDTO req, HttpServletResponse resp) {
        setUserIndexPath(req);

        int total = practicePersonMapper.countByConditions(req);
        if (total == 0) {
            throw new InvalidParameterException("导出记录为空");
        }

        // 限制最大导出数量
        total = Math.min(total, excelMaxExportRows);
        req.setLimit(excelMaxExportRows);

        // 配置excel的head信息和字段转换
        List<Column<PracticePersonListDTO>> columns = new ArrayList<>();
        columns.add(new Column<>("序号", 4000, (index, value) -> index));
        columns.add(new Column<>("实习状态", 4000, (index, value) -> PracticeStatus.getNameOfValue(
                value.getPracticeStatus())));
        columns.add(new Column<>("学生姓名", 4000, (index, value) -> value.getPersonName()));
        columns.add(new Column<>("学号", 4000, (index, value) -> value.getPersonNo()));
        columns.add(new Column<>("实习时段", 8000, (index, value) ->
                value.getStartDate() + " - " + value.getEndDate()));
        columns.add(new Column<>("所属组织", 8000, (index, value) -> value.getOrgPathName()));
        columns.add(new Column<>("实习单位", 8000, (index, value) -> value.getPracticeCompany()));
        columns.add(new Column<>("单位联系人", 8000, (index, value) -> value.getCompanyContacts()));
        columns.add(new Column<>("联系人电话", 8000, (index, value) -> value.getContactsPhone()));

        try {
            ExcelExportHelper<PracticePersonListDTO> handler = new ExcelExportHelper<>(columns, total,
                    resp.getOutputStream());
            handler.setProgressListener(progress -> log.info("实习统计导出进度：" + progress));
            practicePersonMapper.exportByConditions(req, handler);
            handler.done();
        } catch (IOException e) {
            log.error("", e);
            throw new BaseException("导出失败");
        }
    }
}
