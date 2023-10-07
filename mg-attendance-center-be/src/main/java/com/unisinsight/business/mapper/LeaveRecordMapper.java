package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.LeaveInfo;
import com.unisinsight.business.dto.LeaveInfoDTO;
import com.unisinsight.business.dto.request.LeaveRecordQueryReqDTO;
import com.unisinsight.business.dto.request.LeaveStatListReqDTO;
import com.unisinsight.business.dto.response.LeaveRecordDetailDTO;
import com.unisinsight.business.dto.response.LeaveRecordListDTO;
import com.unisinsight.business.dto.response.LeaveStatListResDTO;
import com.unisinsight.business.dto.response.LeaveStudentDTO;
import com.unisinsight.business.model.LeaveRecordDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 请假记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/10
 * @since 1.0
 */
@Repository
public interface LeaveRecordMapper extends Mapper<LeaveRecordDO>, InsertUseGeneratedKeysMapper<LeaveRecordDO> {

    /**
     * 根据主键查找
     *
     * @param id 主键
     * @return 请假记录详情
     */
    LeaveRecordDetailDTO findById(@Param("id") Integer id);

    /**
     * 条件查询
     *
     * @param req 参数
     * @return 记录列表
     */
    List<LeaveRecordListDTO> findByConditions(LeaveRecordQueryReqDTO req);

    /**
     * 查找核实请假详情
     *
     * @param id 主键
     * @return 核实请假详情
     */
    LeaveInfo findLeaveInfoById(@Param("id") Integer id);

    /**
     * 查询人员在一个时间段内是否有已存在的记录（不包括被审核拒绝的）
     *
     * @param personNos 人员编号列表
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 结果列表 返回人员姓名、时间段 ，例：王毅（2021-01-23 - 2021-01-05）
     */
    List<String> checkRepeat(@Param("personNos") List<String> personNos,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    /**
     * 查找人员的请假记录
     *
     * @param personNo 人员编号
     * @param date     日期
     * @return 审批通过的请假记录
     */
    LeaveInfoDTO findByPersonAtDate(@Param("personNo") String personNo,
                                    @Param("date") LocalDate date);

    /**
     * 筛选当天处于请假的人员编号集合
     */
    Set<String> findLeavingPersonsAtDate(@Param("personNos") List<String> personNos,
                                         @Param("date") LocalDate date);
    /**
     * 根据班级查找请假学生
     */
    List<LeaveStudentDTO> findByClasses(@Param("classOrgIndexes") List<String> classOrgIndexes,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("searchKey") String searchKey);

    /**
     * 计算请假状态
     */
    void calcLeaveState();

    /**
     * 请假统计 - 分页查询
     */
    List<LeaveStatListResDTO> getStatList(LeaveStatListReqDTO req);
}
