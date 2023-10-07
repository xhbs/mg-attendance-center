package com.unisinsight.business.job;

import com.alibaba.fastjson.JSON;
import com.unisinsight.business.bo.DailyAttendancePeriodStuBO;
import com.unisinsight.business.bo.DailyAttendanceStaticLevelBO;
import com.unisinsight.business.bo.StaticDateBO;
import com.unisinsight.business.common.utils.TransferDataUtils;
import com.unisinsight.business.dto.PersonCountDTO;
import com.unisinsight.business.mapper.DailyAttendancePeriodStuMapper;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.mapper.PersonMapper;
import com.unisinsight.business.model.DailyAttendancePeriodStuDO;
import com.unisinsight.business.model.DailyAttendanceStaticLevelDO;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.service.IDailyAttendanceStaticLevelServcie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日常考勤高级节点缺勤结果生成
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/8/16
 */
@Component
@Slf4j
public class DailyAttendanceHighLvlInputDataJob {
    @Autowired
    private DailyAttendancePeriodStuMapper dailyAttendancePeriodStuMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private TransferDataUtils transferDataUtils;
    @Autowired
    private IDailyAttendanceStaticLevelServcie dailyAttendanceStaticLevelServcie;
    @Resource
    private PersonMapper personMapper;

    @Scheduled(cron = "${cron.DailyAttendanceHighLvlInputDataJob}")
//    @Scheduled(cron = "* * * * * ?")
    public void run() {
        log.info("[定时任务] - 各级生成日常考勤结果 ===>");
        //获取上周日时间
        LocalDate lastWeekDay = LocalDate.now().minusWeeks(1).with(DayOfWeek.SUNDAY);
        if (! transferDataUtils.checkOpenSchool(lastWeekDay)) {
            log.info("不在考勤周期内，不进行统计！");
            return;
        }
        StaticDateBO staticDateBO = transferDataUtils.getStaticDateBO(lastWeekDay);
        log.info("组织节点考勤统计，日期参数{}",staticDateBO);

        long startTimeMills = System.currentTimeMillis();

        for(short subType=5; subType >= 1;subType--){
            if(subType==5){//先导入最高节点的数据,依赖学生考勤统计表（班级表）
                inputDataMaxLvl(subType,staticDateBO);
            }else{
                inputLowDataLvl(subType,staticDateBO);//低级节点依赖高级节点导数
            }
        }

        log.info("[定时任务] - 高级节点日常考勤统计结果，耗时：{}s <===", (System.currentTimeMillis() -startTimeMills) / 1000);

    }


    /**
     *@description 低级节点导数
     *@param
     *@return
     *@date    2021/8/17 14:50
     */
    private void inputLowDataLvl(short subType,StaticDateBO staticDateBO){
        int offset = 0;
        int limit = 1000;

        //查询低级节点的数据
        DailyAttendanceStaticLevelBO queryLowLvlBO = new DailyAttendanceStaticLevelBO();
        queryLowLvlBO.setSchoolYear(staticDateBO.getSchoolYear());
        queryLowLvlBO.setSchoolTerm(staticDateBO.getSchoolTerm());
        queryLowLvlBO.setYearMonth(staticDateBO.getYearMonth());
        queryLowLvlBO.setSubType(subType);
        //查询高级节点的数据
        DailyAttendanceStaticLevelBO queryHighLvlBO = new DailyAttendanceStaticLevelBO();
        queryHighLvlBO.setSchoolYear(staticDateBO.getSchoolYear());
        queryHighLvlBO.setSchoolTerm(staticDateBO.getSchoolTerm());
        queryHighLvlBO.setYearMonth(staticDateBO.getYearMonth());
        //低级节点是否已统计
        boolean existFlag = dailyAttendanceStaticLevelServcie.checkWeekExist(queryLowLvlBO);

        List<String> parentOrgIndexs = new ArrayList<>(1024);
        Map<String,OrganizationDO> parentOrgMap = new HashMap<>(2048);
        List<DailyAttendanceStaticLevelDO> insertLvlDOs = new ArrayList<>(1024);
        List<DailyAttendanceStaticLevelDO> updateLvlDOs = new ArrayList<>(1024);
        while (true){
            //一次查询100条数据，后续优化，可放大limit，注意outmemory
            //查询低级节点 orgindex
            List<OrganizationDO> organizationDOS = organizationMapper.selectOrganizationsBatch(
                    subType, limit, offset);
            if(CollectionUtils.isEmpty(organizationDOS)){
                break;
            }
            organizationDOS.forEach(item ->{
                parentOrgIndexs.add(item.getOrgIndex());
                parentOrgMap.put(item.getOrgIndex(),item);
            });

            queryHighLvlBO.setOrgParentIndexs(parentOrgIndexs);//查询该节点下的数据（高级）
            List<DailyAttendanceStaticLevelBO> resultHighLvlList = dailyAttendanceStaticLevelServcie.findDailyAttendanceStaticHighLvlList(queryHighLvlBO);
            if(! existFlag){//数据不存在，全部新增
                for (String orgIndex: parentOrgIndexs){
                    //创建StaticLevelBaseDO插入对象
                    DailyAttendanceStaticLevelDO insertLvlDO = initDailyAttendanceStaticLevelBaseDO(staticDateBO);
                    insertLvlDO.setSubType(subType);
                    insertLvlDOs.add(insertLvlDO);
                    setOrgInfo(insertLvlDO, parentOrgMap.get(orgIndex));
                    this.higlLvlGroupByIndex(insertLvlDO,resultHighLvlList);//分组统计orgindex
                }
            }else{
                queryLowLvlBO.setOrgParentIndexs(null);
                queryLowLvlBO.setOrgIndexs(parentOrgIndexs);
                //查询低级节点的数据
                List<DailyAttendanceStaticLevelBO> alreadyLowLvlList
                        = dailyAttendanceStaticLevelServcie.findDailyAttendanceStaticHighLvlList(queryLowLvlBO);
                //list转为map,orgIndex为key,DailyAttendanceStaticLevelBO为value
                Map<String, DailyAttendanceStaticLevelBO> alreadyHighLvlMap  = alreadyLowLvlList.parallelStream()
                        .collect(Collectors.toMap(DailyAttendanceStaticLevelBO::getOrgIndex, p -> p));
                for (String orgIndex: parentOrgIndexs){
                    DailyAttendanceStaticLevelDO insertLvlDO = initDailyAttendanceStaticLevelBaseDO(staticDateBO);
                    insertLvlDO.setSubType(subType);
                    OrganizationDO parentOrgDO = parentOrgMap.get(orgIndex);
                    setOrgInfo(insertLvlDO,parentOrgDO);
                    this.higlLvlGroupByIndex(insertLvlDO,resultHighLvlList);//按照orgIndex统计
                    DailyAttendanceStaticLevelBO alreadyLvlBO = alreadyHighLvlMap.get(orgIndex);
                    if(ObjectUtils.isEmpty(alreadyLvlBO)){//新增记录
                        insertLvlDOs.add(insertLvlDO);
                    }else{//判断更新记录
                        updateLvlDOList(insertLvlDO,alreadyLvlBO,updateLvlDOs);
                    }
                }
            }


            if(! CollectionUtils.isEmpty(insertLvlDOs)){//插入数据
                log.info("插入daily_attendance_static_level表，节点数据条数：{}", insertLvlDOs.size());
                dailyAttendanceStaticLevelServcie.insertList(insertLvlDOs);
            }
            if(! CollectionUtils.isEmpty(updateLvlDOs)){//更新数据
                log.info("更新daily_attendance_static_level表，节点数据条数：{}", updateLvlDOs.size());
                dailyAttendanceStaticLevelServcie.batchUpdate(updateLvlDOs);
            }
            offset += limit;//增加位移
            insertLvlDOs.clear();
            updateLvlDOs.clear();
            parentOrgIndexs.clear();
            parentOrgMap.clear();
        }
    }

    /**
     *@description 先导入最大的节点表
     *@param
     *@return
     *@date    2021/8/17 14:50
     */
    private void inputDataMaxLvl(short subType,StaticDateBO staticDateBO){

        //查询学生周考勤对象
        DailyAttendancePeriodStuBO queryStuBo = new DailyAttendancePeriodStuBO();
        queryStuBo.setSchoolYear(staticDateBO.getSchoolYear());
        queryStuBo.setSchoolTerm(staticDateBO.getSchoolTerm());
        queryStuBo.setCheckWeek(staticDateBO.getCheckWeek());
        //每月的1号
        queryStuBo.setCreateTimeSt(transferDataUtils.getCreateDateSt(staticDateBO));
        //本月的最后一个周日再加7天
        queryStuBo.setCreateTimeEd(transferDataUtils.getCreateDateEd(staticDateBO));
        //查询班级周考勤对象
        DailyAttendanceStaticLevelBO queryClassBO = new DailyAttendanceStaticLevelBO();
        queryClassBO.setSchoolYear(staticDateBO.getSchoolYear());
        queryClassBO.setSchoolTerm(staticDateBO.getSchoolTerm());
        //月份
        queryClassBO.setYearMonth(staticDateBO.getYearMonth());
        //5-班级
        queryClassBO.setSubType(subType);
        //查询当前学年,当前学期,当前月份是否有班级统计数据
        boolean existFlag = dailyAttendanceStaticLevelServcie.checkWeekExist(queryClassBO);
        List<DailyAttendanceStaticLevelDO> insertLvlDOs = new ArrayList<>(1024);
        List<DailyAttendanceStaticLevelDO> updateLvlDOs = new ArrayList<>(1024);
        //班级组织id集合
        List<String> parentOrgIndexs = new ArrayList<>(2048);
        //key:班级id,value:班级组织信息
        Map<String,OrganizationDO> parentOrgMap = new HashMap<>(2048);
        int offset = 0;
        int limit = 1000;
        while (true){
            //一次查询100条数据，后续优化，可放大limit，注意outmemory
            //查询低级组织节点 orgindex, 查询班级组织信息
            List<OrganizationDO> organizationDOS = organizationMapper.selectOrganizationsBatch(Short.valueOf(""+subType), limit, offset);
            if(CollectionUtils.isEmpty(organizationDOS)){
                break;
            }
            organizationDOS.forEach(item ->{
                parentOrgIndexs.add(item.getOrgIndex());
                parentOrgMap.put(item.getOrgIndex(),item);
            });
            queryStuBo.setOrgIndexs(parentOrgIndexs);
            queryClassBO.setOrgIndexs(parentOrgIndexs);
            //用于计算学生在校率,查询时间范围内,指定考勤周期的,指定学年和学期的,所有班级组织下的学生
            List<DailyAttendancePeriodStuDO> stuAttendanceList = dailyAttendancePeriodStuMapper.findStuAttendanceList(queryStuBo);
            //统计在籍班级学生,查询班级下的学生数量
            List<PersonCountDTO> personCountList = personMapper.selectCountGroupByOrgIndex(true,null,parentOrgIndexs);
            //key为班级id,value为在籍学生数量
            Map<String, Integer> registCountMap = personCountList.parallelStream().collect(Collectors.toMap(PersonCountDTO::getOrgIndex, PersonCountDTO::getPersonNum));

            if(! existFlag){//数据不存在，全部新增
                for (String orgIndex: parentOrgIndexs){
                    //创建StaticLevelBaseDO插入对象
                    DailyAttendanceStaticLevelDO insertLvlDO = initDailyAttendanceStaticLevelBaseDO(staticDateBO);
                    insertLvlDO.setSubType(subType);
                    //在籍学生数量
                    int registStudentNum = registCountMap.get(orgIndex) == null ? 0 : registCountMap.get(orgIndex);
                    insertLvlDO.setRegistStudentNum(registStudentNum);
                    insertLvlDOs.add(insertLvlDO);
                    OrganizationDO parentOrgDO = parentOrgMap.get(orgIndex);
                    //赋值所属班级信息
                    this.setOrgInfo(insertLvlDO,parentOrgDO);
                    //计算在校率,以及最新学生总数
                    this.stuGroupByIndex(insertLvlDO,stuAttendanceList);
                }
            }else {
                queryClassBO.setOrgIndexs(parentOrgIndexs);
                //查询已经存在的数据
                List<DailyAttendanceStaticLevelBO> alreadyStaticHighLvlList
                        = dailyAttendanceStaticLevelServcie.findDailyAttendanceStaticHighLvlList(queryClassBO);
                //list转为map,orgIndex为key,DailyAttendanceStaticLevelBO为value
                Map<String, DailyAttendanceStaticLevelBO> alreadyHighLvlMap  = alreadyStaticHighLvlList.parallelStream()
                        .collect(Collectors.toMap(DailyAttendanceStaticLevelBO::getOrgIndex, p -> p));

                for (String orgIndex: parentOrgIndexs){
                    //初始化对象 用于新增
                    DailyAttendanceStaticLevelDO insertLvlDO = initDailyAttendanceStaticLevelBaseDO(staticDateBO);
                    insertLvlDO.setSubType(subType);
                    //获取在籍学生数量
                    int registStudentNum = registCountMap.get(orgIndex) == null ? 0 : registCountMap.get(orgIndex);
                    insertLvlDO.setRegistStudentNum(registStudentNum);
                    //获取组织数据
                    OrganizationDO parentOrgDO = parentOrgMap.get(orgIndex);
                    setOrgInfo(insertLvlDO,parentOrgDO);
                    this.stuGroupByIndex(insertLvlDO,stuAttendanceList);//按照orgIndex统计
                    DailyAttendanceStaticLevelBO alreadyLvlBO = alreadyHighLvlMap.get(orgIndex);
                    if(ObjectUtils.isEmpty(alreadyLvlBO)){//新增记录
                        insertLvlDOs.add(insertLvlDO);
                    }else{//判断更新记录
                        updateLvlDOList(insertLvlDO,alreadyLvlBO,updateLvlDOs);
                    }
                }
            }


            if(! CollectionUtils.isEmpty(insertLvlDOs)){//插入数据
                log.info("daily_attendance_static_level 插入数据条数:{}" ,insertLvlDOs.size());
                dailyAttendanceStaticLevelServcie.insertList(insertLvlDOs);
            }
            if(! CollectionUtils.isEmpty(updateLvlDOs)){//更新数据
                log.info("daily_attendance_static_level 更新数据条数:{}" ,updateLvlDOs.size());
                dailyAttendanceStaticLevelServcie.batchUpdate(updateLvlDOs);
            }
            offset += limit;//增加位移
            insertLvlDOs.clear();
            updateLvlDOs.clear();
            parentOrgIndexs.clear();
            parentOrgMap.clear();
        }
    }

    /**
     *@description 高级节点分组统计
     *@param
     *@return
     *@date    2021/8/27 16:04
     */
    private void higlLvlGroupByIndex(DailyAttendanceStaticLevelDO insertLvlDO,List<DailyAttendanceStaticLevelBO> resultHighLvlList){
        for (DailyAttendanceStaticLevelBO higlLvlBO : resultHighLvlList ){//统计高级节点下的数据
            if(insertLvlDO.getOrgIndex().equals(higlLvlBO.getOrgParentIndex())){
                computeLowLvlRange(higlLvlBO,insertLvlDO);
            }
        }
    }

    /**
     *@description 学生分组统计
     *@param
     *@return
     *@date    2021/8/27 15:29
     */
    private void stuGroupByIndex(DailyAttendanceStaticLevelDO insertLvlDO,List<DailyAttendancePeriodStuDO> stuAttendanceList){
        for (DailyAttendancePeriodStuDO stuDO :stuAttendanceList ){//分组筛选
            if(insertLvlDO.getOrgIndex().equals( stuDO.getOrgIndex())){
                computeMaxLvlRange(stuDO,insertLvlDO);
            }
        }
    }

    /**
     *@description  创建StaticLevelBaseDO对象并初始化
     *@param
     *@return
     *@date    2021/8/17 20:35
     */
    private DailyAttendanceStaticLevelDO initDailyAttendanceStaticLevelBaseDO(StaticDateBO staticDateBO){
        DailyAttendanceStaticLevelDO insertLvlDO = new DailyAttendanceStaticLevelDO();
        insertLvlDO.setSchoolYear(staticDateBO.getSchoolYear());//时间赋值
        insertLvlDO.setSchoolTerm(staticDateBO.getSchoolTerm());//时间赋值
        insertLvlDO.setYearMonth(staticDateBO.getYearMonth());
        insertLvlDO.setCheckWeek(staticDateBO.getCheckWeek());
        insertLvlDO.setRange1(0);
        insertLvlDO.setRange2(0);
        insertLvlDO.setRange3(0);
        insertLvlDO.setRange4(0);
        insertLvlDO.setRange5(0);
        insertLvlDO.setRange6(0);
        insertLvlDO.setRange7(0);
        insertLvlDO.setRange8(0);
        insertLvlDO.setStudentNum(0);
        insertLvlDO.setRegistStudentNum(0);
        return  insertLvlDO;
    }

    /**
     *@description 更新节点数据
     *@param
     *@return
     *@date    2021/8/27 15:54
     */
    private void updateLvlDOList(DailyAttendanceStaticLevelDO insertLvlDO,DailyAttendanceStaticLevelBO alreadyLvlBO,
                                 List<DailyAttendanceStaticLevelDO> updateLvlDOs){
        boolean updateFlag = false;
        if(alreadyLvlBO.getCheckWeek() != insertLvlDO.getCheckWeek() ){
            alreadyLvlBO.setCheckWeek(insertLvlDO.getCheckWeek());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange1() != insertLvlDO.getRange1() ){
            alreadyLvlBO.setRange1(insertLvlDO.getRange1());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange2() != insertLvlDO.getRange2() ){
            alreadyLvlBO.setRange2(insertLvlDO.getRange2());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange3() != insertLvlDO.getRange3() ){
            alreadyLvlBO.setRange3(insertLvlDO.getRange3());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange4() != insertLvlDO.getRange4() ){
            alreadyLvlBO.setRange4(insertLvlDO.getRange4());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange5() != insertLvlDO.getRange5() ){
            alreadyLvlBO.setRange5(insertLvlDO.getRange5());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange6() != insertLvlDO.getRange6() ){
            alreadyLvlBO.setRange6(insertLvlDO.getRange6());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange7() != insertLvlDO.getRange7() ){
            alreadyLvlBO.setRange7(insertLvlDO.getRange7());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRange8() != insertLvlDO.getRange8() ){
            alreadyLvlBO.setRange8(insertLvlDO.getRange8());
            updateFlag = true;
        }
        if(alreadyLvlBO.getStudentNum() != insertLvlDO.getStudentNum() ){
            alreadyLvlBO.setStudentNum(insertLvlDO.getStudentNum());
            updateFlag = true;
        }
        if(alreadyLvlBO.getRegistStudentNum() != insertLvlDO.getRegistStudentNum() ){
            alreadyLvlBO.setRegistStudentNum(insertLvlDO.getRegistStudentNum());
            updateFlag = true;
        }
        if(updateFlag){
            DailyAttendanceStaticLevelDO updateDO = new DailyAttendanceStaticLevelDO();
            BeanUtils.copyProperties(alreadyLvlBO,updateDO);
            updateLvlDOs.add(updateDO);
        }
    }

    /**
     *@description 计算最高节点范围
     *@param
     *@return
     *@date    2021/8/17 20:32
     */
    private DailyAttendanceStaticLevelDO computeMaxLvlRange(DailyAttendancePeriodStuDO periodStuDO,
                                                            DailyAttendanceStaticLevelDO insertLvlDO ){
        Short normalWeeks = periodStuDO.getNormalWeeks();
        double range = (double) normalWeeks / (periodStuDO.getNormalWeeks()+ periodStuDO.getAbsentWeeks());
        if(range ==  1.0d){
            insertLvlDO.setRange1(insertLvlDO.getRange1()+1);
        }else
        if( range >=  0.9d){
            insertLvlDO.setRange2(insertLvlDO.getRange2()+1);
        }else
        if( range >=  0.7d){
            insertLvlDO.setRange3(insertLvlDO.getRange3()+1);
        }else
        if( range >=  0.5d){
            insertLvlDO.setRange4(insertLvlDO.getRange4()+1);
        }else
        if( range >=  0.3d){
            insertLvlDO.setRange5(insertLvlDO.getRange5()+1);
        }else{
            insertLvlDO.setRange6(insertLvlDO.getRange6()+1);
        }
        //小于0.3
        insertLvlDO.setStudentNum(insertLvlDO.getRange1()+insertLvlDO.getRange2()+insertLvlDO.getRange3()+insertLvlDO.getRange4()
                +insertLvlDO.getRange5()+insertLvlDO.getRange6()+insertLvlDO.getRange7()+insertLvlDO.getRange8());
        return insertLvlDO;
    }

    /**
     *@description 设置orgInfo信息
     *@param
     *@return
     *@date    2021/8/18 17:36
     */
    private void setOrgInfo(DailyAttendanceStaticLevelDO insertLvlDO,OrganizationDO organizationDO){
        insertLvlDO.setSubType(organizationDO.getSubType());
        insertLvlDO.setOrgIndex(organizationDO.getOrgIndex());
        insertLvlDO.setOrgName(organizationDO.getOrgName());
        insertLvlDO.setOrgParentIndex(organizationDO.getOrgParentIndex());
    }

    /**
     *@description 计算高级节点范围
     *@param
     *@return
     *@date    2021/8/17 20:32
     */
    private void computeLowLvlRange(DailyAttendanceStaticLevelBO higlLvlBO,DailyAttendanceStaticLevelDO insertLvlDO ){
        insertLvlDO.setRange1(insertLvlDO.getRange1() + higlLvlBO.getRange1());
        insertLvlDO.setRange2(insertLvlDO.getRange2() + higlLvlBO.getRange2());
        insertLvlDO.setRange3(insertLvlDO.getRange3() + higlLvlBO.getRange3());
        insertLvlDO.setRange4(insertLvlDO.getRange4() + higlLvlBO.getRange4());
        insertLvlDO.setRange5(insertLvlDO.getRange5() + higlLvlBO.getRange5());
        insertLvlDO.setRange6(insertLvlDO.getRange6() + higlLvlBO.getRange6());
        insertLvlDO.setRange7(insertLvlDO.getRange7() + higlLvlBO.getRange7());
        insertLvlDO.setRange8(insertLvlDO.getRange8() + higlLvlBO.getRange8());
        insertLvlDO.setStudentNum(insertLvlDO.getStudentNum()+higlLvlBO.getStudentNum());
        insertLvlDO.setRegistStudentNum(insertLvlDO.getRegistStudentNum()+higlLvlBO.getRegistStudentNum());
    }

}
