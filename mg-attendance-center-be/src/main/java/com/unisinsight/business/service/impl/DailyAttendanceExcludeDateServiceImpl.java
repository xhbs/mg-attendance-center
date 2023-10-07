package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.EspecialStrUtils;
import com.unisinsight.business.common.utils.UserUtils;
import com.unisinsight.business.dto.DailyAttendanceExcludeDateDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateAddReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateQueryReqDTO;
import com.unisinsight.business.dto.request.DailyAttendanceExcludeDateUpdateReqDTO;
import com.unisinsight.business.mapper.DailyAttendanceExcludeDateMapper;
import com.unisinsight.business.model.DailyAttendanceExcludeDateDO;
import com.unisinsight.business.service.DailyAttendanceExcludeDateService;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日常考勤排除日期配置服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/12
 */
@Slf4j
@Service
public class DailyAttendanceExcludeDateServiceImpl implements DailyAttendanceExcludeDateService {

    @Resource
    private DailyAttendanceExcludeDateMapper mapper;

    @Override
    public void add(DailyAttendanceExcludeDateAddReqDTO req) {
        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new InvalidParameterException("开始日期不能晚于结束日期");
        }

        checkExists(req, null);

        User user = UserUtils.mustGetUser();
        DailyAttendanceExcludeDateDO record = new DailyAttendanceExcludeDateDO();
        record.setName(req.getName());
        record.setType(req.getType());
        record.setStartDate(req.getStartDate());
        record.setEndDate(req.getEndDate());
        record.setCreatedAt(LocalDateTime.now());
        record.setCreatedBy(user.getUserCode());
        mapper.insertUseGeneratedKeys(record);
        log.info("{} 添加了日常考勤排除日期配置：{}", user.getUserCode(), record.getId());
    }

    @Override
    public void update(DailyAttendanceExcludeDateUpdateReqDTO req) {
        DailyAttendanceExcludeDateDO record = mapper.selectByPrimaryKey(req.getId());
        if (record == null) {
            throw new InvalidParameterException("节假日不存在");
        }
        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new InvalidParameterException("开始日期不能晚于结束日期");
        }

        checkExists(req, req.getId());

        User user = UserUtils.mustGetUser();
        record.setName(req.getName());
        record.setType(req.getType());
        record.setStartDate(req.getStartDate());
        record.setEndDate(req.getEndDate());
        mapper.updateByPrimaryKeySelective(record);
        log.info("{} 更新了日常考勤排除日期配置：{}", user.getUserCode(), record.getId());
    }

    /**
     * 校验是否存在名称 类型 日期完全一致的配置
     */
    private void checkExists(DailyAttendanceExcludeDateAddReqDTO req, Integer id) {
        Sqls sqls = Sqls.custom()
                .andEqualTo("name", req.getName())
                .andEqualTo("type", req.getType())
                .andEqualTo("startDate", req.getStartDate())
                .andEqualTo("endDate", req.getEndDate());
        if (id != null) {
            sqls.andEqualTo("id", id);
        }

        int count = mapper.selectCountByCondition(Example.builder(DailyAttendanceExcludeDateDO.class)
                .where(sqls)
                .build());
        if (count > 0) {
            throw new InvalidParameterException("已存在相同的配置");
        }
    }

    @Override
    public PaginationRes<DailyAttendanceExcludeDateDTO> query(DailyAttendanceExcludeDateQueryReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());

        Sqls sqls = Sqls.custom();
        if (req.getType() != null) {
            sqls.andEqualTo("type", req.getType());
        }
        if (StringUtils.isNotEmpty(req.getName())) {
            sqls.andLike("name", "%" + EspecialStrUtils.change(req.getName()) + "%");
        }

        Example example = Example.builder(DailyAttendanceExcludeDateDO.class)
                .where(sqls)
                .orderByDesc("id")
                .build();
        List<DailyAttendanceExcludeDateDTO> data = mapper.selectByCondition(example)
                .stream()
                .map(src -> {
                    DailyAttendanceExcludeDateDTO dst = new DailyAttendanceExcludeDateDTO();
                    dst.setId(src.getId());
                    dst.setStartDate(src.getStartDate());
                    dst.setEndDate(src.getEndDate());
                    dst.setType(src.getType());
                    dst.setName(src.getName());
                    return dst;
                })
                .collect(Collectors.toList());
        return PaginationRes.of(data, page);
    }

    @Override
    public void batchDelete(List<Integer> ids) {
        int deleted = mapper.deleteByCondition(Example.builder(DailyAttendanceExcludeDateDO.class)
                .where(Sqls.custom()
                        .andIn("id", ids))
                .build());
        User user = UserUtils.mustGetUser();
        log.info("{} 删除了 {} 条日常考勤排除日期配置", user.getUserCode(), deleted);
    }
}
