package com.unisinsight.business.job;

import com.unisinsight.business.bo.SubsidCompareRuleBO;
import com.unisinsight.business.mapper.*;
import com.unisinsight.business.model.SubsidCompareRuleDO;
import com.unisinsight.business.model.SubsidMatchStaticStuDO;
import com.unisinsight.business.model.SubsidStuListDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.time.LocalDate;
import java.util.List;

/**
 * 资助比对清除数据
 *
 * @author tanggang [tang.gang@unisinsight.com]
 * @date 2021/9/8
 */
@Component
@Slf4j
public class SubsidClearDataDataJob {

    @Autowired
    private SubsidCompareRuleMapper subsidCompareRuleMapper;

    @Autowired
    private SubsidStuListMapper subsidStuListMapper;

    @Autowired
    private SubsidStuAttendanceResultsMapper subsidStuAttendanceResultsMapper;

    @Autowired
    private SubsidMatchStaticStuMapper subsidMatchStaticStuMapper;

    @Autowired
    private SubsidMatchStaticLevelMapper subsidMatchStaticLevelMapper;

    @Value("${retention.subsid.common}")
    private Integer commonRetentionDay;

    @Value("${retention.subsid.stu-list}")
    private Integer stulistRetention;

    @Scheduled(cron = "${cron.SubsidClearDataDataJob}")
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("资助比对清除过期数据定时任务开始");
        SubsidCompareRuleBO queryRuleBO = new SubsidCompareRuleBO();
        //资助比对删除40天前的数据
        LocalDate delDate = LocalDate.now().minusDays(commonRetentionDay);
        queryRuleBO.setCreateTimeEd(delDate);
        log.info("删除数据截止日期:{}",delDate);
        List<SubsidCompareRuleDO> subsidCompareRuleDOList = subsidCompareRuleMapper.selectSubsidCompareRuleList(queryRuleBO);
        for (SubsidCompareRuleDO subsidCompareRuleDO : subsidCompareRuleDOList) {
            //删除subsid_compare_rule
            subsidCompareRuleMapper.deleteByPrimaryKey(subsidCompareRuleDO.getId());

            //删除subsid_stu_list
            Condition subSidStuListcondition = new Condition(SubsidStuListDO.class);
            if (0 == subsidCompareRuleDO.getSubsidType()) {//0-自动比对留存的名单特殊处理
                subSidStuListcondition.createCriteria().andEqualTo("subListIndex", subsidCompareRuleDO.getSubListIndex())
                        .andLessThanOrEqualTo("createTime", LocalDate.now().minusDays(stulistRetention));
            } else {//手工比对上传的名单，每次上传都不一样，留存一天就够了
                subSidStuListcondition.createCriteria().andLessThanOrEqualTo("createTime", LocalDate.now().minusDays(1));
            }
            subsidStuListMapper.deleteByCondition(subSidStuListcondition);

            //删除 subsid_match_static_stu
            Condition subsidMathStaticStucondition = new Condition(SubsidMatchStaticStuDO.class);
            subsidMathStaticStucondition.createCriteria().andEqualTo("subsidRuleId", subsidCompareRuleDO.getId());
            subsidMatchStaticStuMapper.deleteByCondition(subsidMathStaticStucondition);

            //删除 subsid_match_static_level
            Condition subsidMathStaticLevelCondition = new Condition(SubsidMatchStaticStuDO.class);
            subsidMathStaticLevelCondition.createCriteria().andEqualTo("subsidRuleId", subsidCompareRuleDO.getId());
            subsidMatchStaticLevelMapper.deleteByCondition(subsidMathStaticLevelCondition);

            //删除 subsid_stu_attendance_results
            Condition subsidAttendanceCondition = new Condition(SubsidMatchStaticStuDO.class);
            subsidAttendanceCondition.createCriteria().andEqualTo("subsidRuleId", subsidCompareRuleDO.getId());
            subsidStuAttendanceResultsMapper.deleteByCondition(subsidAttendanceCondition);
        }

        log.info("资助比对清除过期数据定时任务开始");

    }
}
