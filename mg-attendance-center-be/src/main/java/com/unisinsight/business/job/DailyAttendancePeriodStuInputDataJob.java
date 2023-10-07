package com.unisinsight.business.job;

import com.alibaba.fastjson.JSON;
import com.unisinsight.business.bo.DailyAttendancePeriodStuBO;
import com.unisinsight.business.bo.FindStuWeekResultParamBO;
import com.unisinsight.business.bo.PersonBO;
import com.unisinsight.business.bo.StaticDateBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.common.utils.TransferDataUtils;
import com.unisinsight.business.dto.StuWeekResultCountDTO;
import com.unisinsight.business.dto.request.PersonReqDTO;
import com.unisinsight.business.mapper.DailyAttendancePeriodStuMapper;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.model.DailyAttendancePeriodStuDO;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.service.DailyAttendanceResultService;
import com.unisinsight.business.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日常考勤缺勤结果生成定时任务
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/8/16
 */
@Component
@Slf4j
public class DailyAttendancePeriodStuInputDataJob {

    @Autowired
    private DailyAttendanceResultService dailyAttendanceResultService;

    @Autowired
    private DailyAttendancePeriodStuMapper dailyAttendancePeriodStuMapper;

    @Autowired
    private PersonService personService;

    @Autowired
    private TransferDataUtils transferDataUtils;
    @Autowired
    private OrganizationMapper organizationMapper;
 
    @Scheduled(cron = "${cron.DailyAttendancePeriodStuInputDataJob}")
//    @Scheduled(cron = "* * * * * ?")
    public void run() {
        log.info("[定时任务] - 生成日常考勤结果 ===>");
        LocalDate lastWeekDay = LocalDate.now().minusWeeks(1).with(DayOfWeek.SUNDAY);
        if (!transferDataUtils.checkOpenSchool(lastWeekDay)) {
            log.info("不在考勤周期内，不进行统计！");
            return;
        }
        StaticDateBO staticDateBO = transferDataUtils.getStaticDateBO(lastWeekDay);
        log.info("学生考勤统计，日期参数{}",staticDateBO);

        long startTimeMills = System.currentTimeMillis();
        List<String> personList = new ArrayList<>(2048);//学生列表
        Map<String, PersonBO> presonMap = new HashMap<>(4096);//暂存学生信息
        FindStuWeekResultParamBO dailyAttendanceDetailQueryReqDTO = new FindStuWeekResultParamBO();
        dailyAttendanceDetailQueryReqDTO.setAttendanceStartDate(staticDateBO.getStartDate());//开始日期
        dailyAttendanceDetailQueryReqDTO.setAttendanceEndDate(staticDateBO.getEndDate());//截止日期
        List<DailyAttendancePeriodStuDO> insertPeriodStuDOList = new ArrayList<>(1024);
        List<DailyAttendancePeriodStuDO> updatePeriodStuDOList = new ArrayList<>(1024);
        Map<String,String> orgNameMap = new HashMap(16384);//组织节点名称翻译，大概40w学生，一万个班级左右
        DailyAttendancePeriodStuBO queryStuBO = new DailyAttendancePeriodStuBO();
        queryStuBO.setSchoolYear(staticDateBO.getSchoolYear());
        queryStuBO.setSchoolTerm(staticDateBO.getSchoolTerm());
        queryStuBO.setYearMonth(staticDateBO.getYearMonth());
        queryStuBO.setCreateTimeSt(transferDataUtils.getCreateDateSt(staticDateBO));
        queryStuBO.setCreateTimeEd(transferDataUtils.getCreateDateEd(staticDateBO));

        int i = dailyAttendancePeriodStuMapper.countByConditions(queryStuBO);
        boolean existFlag = false;
        if(i>0){
            existFlag = true;
        }
        queryStuBO.setPersonNos(personList);
        int offset=0;
        while (true){//组织节点名称翻译
            List<OrganizationDO> organizationDOS = organizationMapper.selectOrganizationsBatch(Short.valueOf("5"), 2000, offset);
            if( CollectionUtils.isEmpty(organizationDOS)){
                break;
            }
            organizationDOS.forEach(item->{
                orgNameMap.put(item.getOrgIndex(),item.getOrgName());
            });
            offset+=2000;
        }
        log.info("组织数量{}",orgNameMap.size());
        Integer personTotalNum = personService.selectCountByCondition(new PersonBO());
        if(personTotalNum == null ||personTotalNum ==0){
            log.info("无学生数据！");
            return;
        }
        PersonReqDTO personReqDTO = new PersonReqDTO();//查询学生信息
        personReqDTO.setPageSize(2000);//设置页面大小
        int pageIndex = 1;
        int totalPageNum ;
        if(0 == personTotalNum % personReqDTO.getPageSize()){
            totalPageNum = personTotalNum/personReqDTO.getPageSize();
        }else{
            totalPageNum = personTotalNum/personReqDTO.getPageSize() + 1 ;
        }

        while (pageIndex <= totalPageNum){
            personReqDTO.setPageNum(pageIndex);
            PaginationRes<PersonBO> personPage = personService.query(personReqDTO);//查询学生信息
            if (personPage.getPaging().getPageSize() <= 0) {
                break;
            }
            List<PersonBO> dataList = personPage.getData();
            for (PersonBO personBO : dataList) {
                personList.add(personBO.getPersonNo());
                presonMap.put(personBO.getPersonNo(), personBO);
            }
            dailyAttendanceDetailQueryReqDTO.setPersonNoList(personList);
            //统计学生出勤结果信息
            List<StuWeekResultCountDTO> stuWeekResultList =
                    dailyAttendanceResultService.findStuWeekResultList(dailyAttendanceDetailQueryReqDTO);

            //学生周考勤表结果本周是否已统计

            if(! existFlag){//没有记录全部新增
                for (StuWeekResultCountDTO item : stuWeekResultList){
                    PersonBO personBO = presonMap.get(item.getPersonNo());
                    //插入新增数据
                    this.insertPeriodStuDOList(item,personBO,staticDateBO,orgNameMap,insertPeriodStuDOList);
                }
            }else {//有记录，匹配更新或新增
                //学生统计结果转换成map
                List<DailyAttendancePeriodStuDO> alreadyCountStuAttList = dailyAttendancePeriodStuMapper.findStuAttendanceList(queryStuBO);
                Map<String, DailyAttendancePeriodStuDO> stuAttPeriodMap  = alreadyCountStuAttList.parallelStream().collect(Collectors.toMap(DailyAttendancePeriodStuDO::getPersonNo, attendancePeriodStuDO -> attendancePeriodStuDO));
                for (StuWeekResultCountDTO countDTO : stuWeekResultList){
                    PersonBO personBO = presonMap.get(countDTO.getPersonNo());
                    DailyAttendancePeriodStuDO alreadyPeriodStuDO = stuAttPeriodMap.get(countDTO.getPersonNo());
                    if(ObjectUtils.isEmpty(alreadyPeriodStuDO)){
                        //插入新增数据
                        this.insertPeriodStuDOList(countDTO,personBO,staticDateBO,orgNameMap,insertPeriodStuDOList);
                    }else{//更新数据
                        this.updatePeriodStuDOList(countDTO,alreadyPeriodStuDO,updatePeriodStuDOList,staticDateBO);
                    }
                }
            }

            if(! CollectionUtils.isEmpty(insertPeriodStuDOList)){
                log.info("daily_attendance_static_period_stu 插入数据条数:{}" ,insertPeriodStuDOList.size());
                dailyAttendancePeriodStuMapper.insertList(insertPeriodStuDOList);//插入学生结果统计表
            }
            if(! CollectionUtils.isEmpty(updatePeriodStuDOList)){//批量更新
                log.info("daily_attendance_static_period_stu 更新数据条数:{}" ,updatePeriodStuDOList.size());
                dailyAttendancePeriodStuMapper.batchUpdate(updatePeriodStuDOList);
            }
            personList.clear();
            presonMap.clear();
            insertPeriodStuDOList.clear();
            updatePeriodStuDOList.clear();
            pageIndex++;//增加位移
        }
        log.info("[定时任务] - 生成学生日常考勤结果，耗时：{}s <===", (System.currentTimeMillis()-startTimeMills) / 1000);
    }

    /**
     *@description 设置学生考勤数据记录
     *@param
     *@return
     *@date    2021/8/27 14:23
     */
    private void insertPeriodStuDOList( StuWeekResultCountDTO countDTO,PersonBO personBO,StaticDateBO staticDateBO
            , Map<String,String> orgNameMap, List<DailyAttendancePeriodStuDO> insertPeriodStuDOList){
        DailyAttendancePeriodStuDO dailyAttendancePeriodStuDO = new DailyAttendancePeriodStuDO();
        dailyAttendancePeriodStuDO.setNormalWeeks(countDTO.getNormalWeeks());
        dailyAttendancePeriodStuDO.setAbsentWeeks(countDTO.getAbsentWeeks());
        dailyAttendancePeriodStuDO.setPersonNo(personBO.getPersonNo());
        dailyAttendancePeriodStuDO.setPersonName(personBO.getPersonName());
        dailyAttendancePeriodStuDO.setOrgIndex(personBO.getOrgIndex());
        dailyAttendancePeriodStuDO.setOrgName(orgNameMap.get(personBO.getOrgIndex()));
        dailyAttendancePeriodStuDO.setCheckWeek(staticDateBO.getCheckWeek());
        dailyAttendancePeriodStuDO.setSchoolYear(staticDateBO.getSchoolYear());
        dailyAttendancePeriodStuDO.setSchoolTerm(staticDateBO.getSchoolTerm());
        dailyAttendancePeriodStuDO.setYearMonth(staticDateBO.getYearMonth());
        dailyAttendancePeriodStuDO.setCreateTime(LocalDate.now());
        dailyAttendancePeriodStuDO.setUpdateTime(LocalDate.now());
        insertPeriodStuDOList.add(dailyAttendancePeriodStuDO);
    }

    /**
     *@description 更新考勤记录
     *@param
     *@return
     *@date    2021/8/27 14:23
     */
    private void updatePeriodStuDOList( StuWeekResultCountDTO countDTO,DailyAttendancePeriodStuDO updateStuDO, List<DailyAttendancePeriodStuDO> updatePeriodStuList,StaticDateBO staticDateBO){
        boolean updateFlag = false;
        if(updateStuDO.getNormalWeeks() != countDTO.getNormalWeeks()){
            updateStuDO.setNormalWeeks(countDTO.getNormalWeeks());
            updateFlag = true;
        }
        if(updateStuDO.getAbsentWeeks() != countDTO.getAbsentWeeks()){
            updateStuDO.setAbsentWeeks(countDTO.getAbsentWeeks());
            updateFlag = true;
        }
        if(staticDateBO.getCheckWeek() != updateStuDO.getCheckWeek()){
            updateStuDO.setCheckWeek(staticDateBO.getCheckWeek());
            updateFlag = true;
        }
        if(updateFlag){
            updateStuDO.setUpdateTime(LocalDate.now());
            updatePeriodStuList.add(updateStuDO);
        }
    }
}
