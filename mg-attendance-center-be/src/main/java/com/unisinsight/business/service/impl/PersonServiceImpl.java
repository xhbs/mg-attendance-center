package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.PersonBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.PersonReqDTO;
import com.unisinsight.business.mapper.PersonMapper;
import com.unisinsight.business.model.PersonDO;
import com.unisinsight.business.rpc.dto.OMPersonDTO;
import com.unisinsight.business.service.AttendanceEventFilter;
import com.unisinsight.business.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员从对象管理同步服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/4/12
 * @since 1.0
 */
@Service
@Slf4j
public class PersonServiceImpl implements PersonService {

    @Resource
    private PersonMapper personMapper;

    @Resource
    private AttendanceEventFilter attendanceEventFilter;

    @Override
    public void addSync(List<OMPersonDTO> persons) {
        List<PersonDO> records = persons.stream()
                .map(src -> {
                    PersonDO person = new PersonDO();
                    person.setPersonNo(src.getUserCode());
                    person.setPersonName(src.getUserName());
                    person.setPersonUrl(src.getUserImage());
                    person.setOrgIndex(src.getOrgIndex());
                    person.setRegistered(src.getIsRecord() != null && src.getIsRecord() == 0);
                    person.setAtSchool(src.getIsSchool() != null && src.getIsSchool() == 0);
                    return person;
                }).collect(Collectors.toList());
        personMapper.batchSaveOrUpdate(records);

        // 插入缓存
        attendanceEventFilter.addPersonCache(records.stream()
                .map(PersonDO::getPersonNo)
                .collect(Collectors.toList()));
        log.info("保存 {} 个考勤人员", records.size());
    }

    @Override
    public void updateSync(List<OMPersonDTO> persons) {
        addSync(persons);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByPersonNos(List<String> personNos) {
        personMapper.deleteByCondition(Example.builder(PersonDO.class)
                .where(Sqls.custom()
                        .andIn("personNo", personNos))
                .build());
        // 清理缓存
        attendanceEventFilter.removePersonCache(personNos);
        log.info("删除了 {} 个考勤人员", personNos.size());
    }

    @Override
    public PaginationRes<PersonBO> query(PersonReqDTO req) {
        Page page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<PersonBO> records = personMapper.query(req);
        return PaginationRes.of(records, page);
    }

    @Override
    public Integer selectCountByCondition(PersonBO personBO) {
        PersonDO personDO = new PersonDO();
        BeanUtils.copyProperties(personBO, personDO);
        return personMapper.selectCount(personDO);
    }
}
