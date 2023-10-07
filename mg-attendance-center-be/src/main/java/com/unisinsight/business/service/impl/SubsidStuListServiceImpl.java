/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.unisinsight.business.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unisinsight.business.bo.*;
import com.unisinsight.business.dto.SubsidStuListBaseExcelDTO;
import com.unisinsight.business.dto.request.SubsidStuInfoDTO;
import com.unisinsight.business.dto.request.SubsidStuListSyncReqDTO;
import com.unisinsight.business.dto.response.SubsidListUploadResDTO;
import com.unisinsight.business.mapper.SubsidStuListMapper;
import com.unisinsight.business.model.SubsidStuListDO;
import com.unisinsight.business.service.PersonService;
import com.unisinsight.business.service.SubsidStuListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 *
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/08/31 21:06:31
 * @since 1.0
 */
@Service
@Slf4j
public class SubsidStuListServiceImpl  implements SubsidStuListService {
    @Resource
    private SubsidStuListMapper subsidStuListMapper;
    @Autowired
    private PersonService personService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubsidListUploadResDTO uploadSubsidStuListByHandle(List<SubsidStuListBaseExcelDTO> uploadList , Integer prjType){
        String subListIndex = prjType +""+ System.currentTimeMillis();
        List<SubsidStuListDO> insertList =  new ArrayList<>(1024);
        Date currDate = new Date();
        for (SubsidStuListBaseExcelDTO subsidStuListBaseExcelDTO : uploadList){
            SubsidStuListDO insertDO = new SubsidStuListDO();
            BeanUtils.copyProperties(subsidStuListBaseExcelDTO,insertDO);
            insertDO.setPersonNo(insertDO.getCertNo());
            insertDO.setSubListIndex(subListIndex);
            insertDO.setCreateTime(currDate);
            insertList.add(insertDO);
        }
        if(insertList.size()>1000){
            List<SubsidStuListDO> slipList = new ArrayList<>(1536);
            int index=1;
            for (SubsidStuListDO subsidStuListDO : insertList){
                slipList.add(subsidStuListDO);
                if(index % 1000 == 0){
                    subsidStuListMapper.insertList(slipList);
                    slipList.clear();
                }
                ++index;
            }
            if(!CollectionUtils.isEmpty(slipList)){
                subsidStuListMapper.insertList(slipList);
            }
        }else{
            subsidStuListMapper.insertList(insertList);
        }
        //查找不合法的学生名单
        List<SubsidStuListDO> noLegalSubsidStuList = subsidStuListMapper.findNoLegalSubsidStuList(subListIndex);
        SubsidListUploadResDTO.SubsidListUploadResDTOBuilder builder = SubsidListUploadResDTO.builder();
        if(CollectionUtils.isEmpty(noLegalSubsidStuList)){//上传名单全部成功
            SubsidListUploadResDTO resDTO = builder
                    .respCode(1)
                    .message("名单上传成功")
                    .subListIndex(subListIndex)
                    .build();
            return resDTO;
        }
        if(uploadList.size() == noLegalSubsidStuList.size()){//上传名单全部失败
            SubsidListUploadResDTO resDTO = builder
                    .respCode(0)
                    .message("名单中没有考勤学生")
                    .build();
            return resDTO;
        }

        //上传名单部分异常
        StringBuilder messageBuilder = new StringBuilder();
        for(SubsidStuListDO subsidStuListDO :noLegalSubsidStuList){
            messageBuilder.append(subsidStuListDO.getPersonName());
            messageBuilder.append(",");
        }
        messageBuilder.append("名单部分数据异常");

        SubsidListUploadResDTO resDTO = builder
                .respCode(2)
                .message(messageBuilder.toString())
                .subListIndex(subListIndex)
                .build();
        return resDTO;
    }

    @Override
    public void subsidStuListSync(SubsidStuListSyncReqDTO reqDTO) {
        List<SubsidStuInfoDTO> stulist = reqDTO.getStulist();
        List<SubsidStuListDO> insertList = new ArrayList<>(1024);
        List<SubsidStuListDO> updateList = new ArrayList<>(1024);
        StringBuilder stuListIndexSb = new StringBuilder();
        stuListIndexSb.append(reqDTO.getYearMonth())
                .append(reqDTO.getProType())
                .append(reqDTO.getDataType());
        SubsidStuListDO queryDO = new SubsidStuListDO();
        queryDO.setSubListIndex(stuListIndexSb.toString());
        List<SubsidStuListDO> subsidStuListDOS = subsidStuListMapper.select(queryDO);
        Map<String, SubsidStuListDO> subsidStuMap  = subsidStuListDOS.parallelStream().collect(
                Collectors.toMap(item->{
                    StringBuilder key = new StringBuilder();
                    key.append(item.getSubListIndex())
                            .append(item.getPersonNo());
                        return key.toString();
                    }, attendancePeriodStuDO -> attendancePeriodStuDO));
        String stuListKey = reqDTO.getYearMonth()+reqDTO.getProType()+reqDTO.getDataType();
        for(SubsidStuInfoDTO subsidStuDTO :stulist){
           String stuKey = stuListKey + subsidStuDTO.getPersonNo();
           SubsidStuListDO subsidStuListDO = subsidStuMap.get(stuKey);
           //该学生名单是否存在
           if(ObjectUtils.isEmpty(subsidStuListDO)){
               insertSubsidStuListSync(reqDTO,subsidStuDTO,insertList);
           }else {
               updateSubsidStuListSync(subsidStuDTO,subsidStuListDO,insertList);
           }
        }

        if(!CollectionUtils.isEmpty(insertList)){
            log.info("subsid_stu_list 插入数据条数:{}" ,insertList.size());
            subsidStuListMapper.insertList(insertList);
        }
        if(!CollectionUtils.isEmpty(updateList)){
            subsidStuListMapper.batchUpdate(updateList);
        }
    }

    /**
      *@description 新增同步记录
      *@param
      *@return
      *@date    2021/9/1 13:51
      */
    private void insertSubsidStuListSync(SubsidStuListSyncReqDTO  reqDTO, SubsidStuInfoDTO subsidStuDTO, List<SubsidStuListDO> insertList){
        PersonBO queryPersonBO = new PersonBO();
        queryPersonBO.setPersonNo(subsidStuDTO.getPersonNo());
        Integer integer = personService.selectCountByCondition(queryPersonBO);
        if(integer <= 0){
            return;
        }
        StringBuilder stuListIndexSb = new StringBuilder();
        stuListIndexSb.append(reqDTO.getYearMonth())
                .append(reqDTO.getProType())
                .append(reqDTO.getDataType());
        SubsidStuListDO insertDO = new SubsidStuListDO();
        insertDO.setSubListIndex(stuListIndexSb.toString());
        insertDO.setPersonNo(subsidStuDTO.getPersonNo());
        insertDO.setPersonName(subsidStuDTO.getPersonName());
        insertList.add(insertDO);
    }


    /**
      *@description 更新资助学生数据
      *@param
      *@return
      *@date    2021/9/1 11:31
      */
    private void updateSubsidStuListSync(SubsidStuInfoDTO subsidStuInfoDTO, SubsidStuListDO updateDO, List<SubsidStuListDO> updateList){
        boolean updateFlag = false;
        if(! updateDO.getPersonName().equals(subsidStuInfoDTO.getPersonName())){
            updateDO.setPersonName(subsidStuInfoDTO.getPersonName());
            updateFlag =true;
        }
        if(! updateDO.getCertNo().equals(subsidStuInfoDTO.getCertNo())){
            updateDO.setCertNo(subsidStuInfoDTO.getCertNo());
            updateFlag =true;
        }
        if(updateFlag){
            updateList.add(updateDO);
        }
    }

    @Override
    public PaginationRes<SubsidStuListDO> getSubsidStuExcelPage(QuerySubsidStuBO queryBO, PaginationReq paginationReq) {
        Page page = PageHelper.startPage(paginationReq.getPageNum(), paginationReq.getPageSize());
        List<SubsidStuListDO> subsidList = subsidStuListMapper.findSubsidStuExcelList(queryBO);
        return PaginationRes.of(subsidList, page);
    }

    @Override
    public PaginationRes<SubsidStuBO> getPage(String subListIndex, PaginationReq paginationReq) {
        Page page = PageHelper.startPage(paginationReq.getPageNum(), paginationReq.getPageSize());
        List<SubsidStuBO> subsidList = subsidStuListMapper.findSubsidStuListBySubListIndex(subListIndex);
        return PaginationRes.of(subsidList, page);
    }

    @Override
    public PaginationRes<String> getPersonNoListPage(String subListIndex, PaginationReq paginationReq) {
        Page page = PageHelper.startPage(paginationReq.getPageNum(), paginationReq.getPageSize());
        List<String> personList = subsidStuListMapper.findPersonListBySubListIndex(subListIndex);
        return PaginationRes.of(personList, page);
    }
    @Override
    public List<SubsidStuCountBO> findSubListIndex() {
        List<SubsidStuCountBO> list = subsidStuListMapper.findSubListIndex();
        list.stream().filter(e->e.getSubListIndex().length()==8).forEach(e->{
            String subListIndex = e.getSubListIndex();
            String year = subListIndex.substring(0, 4);
            String month = subListIndex.substring(5, 7);
            String type = subListIndex.substring(7, 8);
            type = "0".equals(type) ? "免学费" : "国家助学金";
            String desc = year + "年" + month + "月" + type;
            e.setDesc(desc);
        });
        return list;
    }
}
