package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.enums.OrgType;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.request.AppealPersonListReqDTO;
import com.unisinsight.business.dto.request.AppealStatListReqDTO;
import com.unisinsight.business.dto.response.AppealPersonListResDTO;
import com.unisinsight.business.dto.response.AppealStatListResDTO;
import com.unisinsight.business.mapper.AppealPersonMapper;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.service.AppealStatService;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 申诉统计服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/29
 */
@Slf4j
@Service
public class AppealStatServiceImpl implements AppealStatService {

    @Resource
    private AppealPersonMapper appealPersonMapper;

    @Resource
    private OrganizationMapper organizationMapper;

    @Override
    public PaginationRes<AppealStatListResDTO> statByOrg(AppealStatListReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<OrganizationDO> childOrgs = organizationMapper.getChildren(req.getOrgIndex(), req.getSearch());
        if (childOrgs.isEmpty()) {
            return PaginationRes.empty(page);
        }

        List<AppealStatListResDTO> data = statByOrgList(childOrgs, req.getStartDate(), req.getEndDate());
        return PaginationRes.of(data, page);
    }

    private List<AppealStatListResDTO> statByOrgList(List<OrganizationDO> childOrgs, LocalDate startDate,
                                                     LocalDate endDate) {
        Short subType = childOrgs.get(0).getSubType();
        List<AppealStatListResDTO> data;
        if (subType != null && subType.equals(OrgType.CLASS.getValue())) {// 子组织是否是班级
            List<String> orgIndexes = childOrgs.stream()
                    .map(OrganizationDO::getOrgIndex)
                    .collect(Collectors.toList());
            data = appealPersonMapper.statByClass(orgIndexes, startDate, endDate);
        } else {
            List<String> orgIndexPaths = childOrgs.stream()
                    .map(org -> org.getIndexPath() + org.getOrgIndex())
                    .collect(Collectors.toList());
            data = appealPersonMapper.statByOrg(orgIndexPaths, startDate, endDate);
        }

        for (int i = 0; i < data.size(); i++) {
            AppealStatListResDTO item = data.get(i);
            OrganizationDO org = childOrgs.get(i);
            item.setOrgIndex(org.getOrgIndex());
            item.setOrgName(org.getOrgName());
            item.setSubType(org.getSubType());
        }
        return data;
    }

    @Override
    public void exportByOrg(AppealStatListReqDTO req, HttpServletResponse resp) {
        User user = UserUtils.mustGetUser();
        List<OrganizationDO> orgList = organizationMapper.getChildren(req.getOrgIndex(), req.getSearch());
        if (orgList.isEmpty()) {
            throw new BaseException("暂无可导出数据");
        }

        List<AppealStatListResDTO> data = statByOrgList(orgList, req.getStartDate(), req.getEndDate());
        ExcelUtil.export(resp, AppealStatListResDTO.class, data, "申诉统计");
        log.info("{} 导出了 {} 条申诉统计记录，查询参数：{}", user.getUserCode(), data.size(), req);
    }

    @Override
    public PaginationRes<AppealPersonListResDTO> statByPerson(AppealPersonListReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<AppealPersonListResDTO> data = appealPersonMapper.statByPerson(req);
        return PaginationRes.of(data, page);
    }

    @Override
    public void exportByPerson(AppealPersonListReqDTO req, HttpServletResponse resp) {
        User user = UserUtils.mustGetUser();
        OrganizationDO classOrg = organizationMapper.selectByOrgIndex(req.getOrgIndex());
        if (classOrg == null) {
            throw new BaseException("组织不存在");
        }

        if (classOrg.getSubType() != OrgType.CLASS.getValue()) {
            throw new BaseException("该组织不是班级");
        }

        List<AppealPersonListResDTO> data = appealPersonMapper.statByPerson(req);
        ExcelUtil.export(resp, AppealPersonListResDTO.class, data, "申诉统计" + classOrg.getOrgName());
        log.info("{} 导出了 {} 条申诉统计记录，查询参数：{}", user.getUserCode(), data.size(), req);
    }
}
