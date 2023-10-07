package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.common.utils.excel.ExcelUtil;
import com.unisinsight.business.dto.request.LeaveStatListReqDTO;
import com.unisinsight.business.dto.response.LeaveStatListResDTO;
import com.unisinsight.business.mapper.LeaveRecordMapper;
import com.unisinsight.business.service.LeaveStatService;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 请假统计服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/23
 */
@Slf4j
@Service
public class LeaveStatServiceImpl implements LeaveStatService {

    @Resource
    private LeaveRecordMapper leaveRecordMapper;

    @Override
    public PaginationRes<LeaveStatListResDTO> list(LeaveStatListReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<LeaveStatListResDTO> data = leaveRecordMapper.getStatList(req);
        return PaginationRes.of(data, page);
    }

    @Override
    public void export(LeaveStatListReqDTO req, HttpServletResponse resp) {
        User user = UserUtils.mustGetUser();
        List<LeaveStatListResDTO> data = leaveRecordMapper.getStatList(req);
        ExcelUtil.export(resp, LeaveStatListResDTO.class, data, "请假统计");
        log.info("{} 导出了 {} 条请假统计记录，查询参数：{}", user.getUserCode(), data.size(), req);
    }
}
