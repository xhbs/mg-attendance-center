package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.bo.PersonListOfClassBO;
import com.unisinsight.business.bo.SpotTaskResultCountBO;
import com.unisinsight.business.dto.request.*;
import com.unisinsight.business.dto.response.SpotCheckTaskDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 抽查考勤任务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/31
 */
public interface SpotCheckTaskService {

    /**
     * 创建
     *
     * @param req 任务参数
     * @return 任务ID
     */
    Integer add(SpotCheckTaskAddReqDTO req);

    SpotTaskResultCountBO selectSpotPersonCount(int taskId);

    Integer addCallTheRoll(SpotCheckTaskAddReqDTO req);

    /**
     * 更新
     *
     * @param req 任务参数
     */
    void update(SpotCheckTaskUpdateReqDTO req);

    /**
     * 查询
     *
     * @param id 主键
     * @return 任务详情
     */
    SpotCheckTaskDTO get(Integer id);

    /**
     * 查询任务的考勤日期
     *
     * @param id 任务ID
     * @return 日期集合
     */
    List<String> getAttendanceDates(Integer id);

    /**
     * 分页查询
     *
     * @param req 入参
     * @return 任务列表
     */
    PaginationRes<SpotCheckTaskDTO> list(SpotCheckTaskListReqDTO req);

    /**
     * 删除
     *
     * @param ids ID集合
     */
    void delete(List<Integer> ids);

    @Transactional(rollbackFor = Exception.class)
    void callTheRoll(CallTheRollDto req);


    PaginationRes<PersonListOfClassBO> stuList(StuListReq req);

    PersonListOfClassBO detail(StuListReq req);
}