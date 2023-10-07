package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.PracticeInfo;
import com.unisinsight.business.dto.PracticeInfoDTO;
import com.unisinsight.business.dto.request.PracticeAttendanceListReqDTO;
import com.unisinsight.business.dto.request.PracticeRecordQueryReqDTO;
import com.unisinsight.business.dto.response.PracticeAttendanceListDTO;
import com.unisinsight.business.dto.response.PracticeRecordDetailDTO;
import com.unisinsight.business.dto.response.PracticeRecordListDTO;
import com.unisinsight.business.dto.response.PracticeStudentDTO;
import com.unisinsight.business.model.PracticeRecordDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 实习申请记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
public interface PracticeRecordMapper extends Mapper<PracticeRecordDO>, InsertUseGeneratedKeysMapper<PracticeRecordDO> {
    /**
     * 根据主键查找
     *
     * @param id 主键
     * @return 实习申请记录详情
     */
    PracticeRecordDetailDTO findById(@Param("id") Integer id);

    /**
     * 查找实习核实详情
     *
     * @param id 主键ID
     * @return 实习信息
     */
    PracticeInfo findPracticeInfoById(@Param("id") Integer id);

    /**
     * 条件查询
     *
     * @param req 参数
     * @return 记录列表
     */
    List<PracticeRecordListDTO> findByConditions(PracticeRecordQueryReqDTO req);

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
     * 查找人员的实习记录
     *
     * @param personNo 人员编号
     * @param date     日期
     * @return 审批通过的实习记录
     */
    PracticeInfoDTO findByPersonAtDate(@Param("personNo") String personNo,
                                       @Param("date") LocalDate date);

    /**
     * 计算实习状态
     */
    void calcPracticeStatus();

    /**
     * 查询实习点名结束的记录
     */
    List<Integer> findAbsenceFinishedRecords(@Param("date") LocalDate date);

    /**
     * 更新点名通过的记录，如果实习申请所关联的人员全部考勤状态都是正常，那么点名通过
     */
    void updatePassedAttendanceRecords(@Param("ids") List<Integer> ids);

    /**
     * 更新点名不通过状态
     */
    void updateFailedAttendanceRecords(@Param("ids") List<Integer> ids);

    /**
     * WEB 实习点名任务列表查询
     */
    List<PracticeAttendanceListDTO> findAttendanceTasks(PracticeAttendanceListReqDTO param);

    /**
     * 根据班级查找实习中的学生
     */
    List<PracticeStudentDTO> findByClasses(@Param("classOrgIndexes") List<String> classOrgIndexes,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("searchKey") String searchKey);
}