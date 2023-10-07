package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.DictBO;
import com.unisinsight.business.bo.PracticeAttendancePersonBO;
import com.unisinsight.business.bo.PracticeAttendanceResultBO;
import com.unisinsight.business.dto.request.PracticeAttendanceDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonDetailsReqDTO;
import com.unisinsight.business.dto.request.PracticeAttendancePersonListReqDTO;
import com.unisinsight.business.dto.request.PracticePersonQueryReqDTO;
import com.unisinsight.business.dto.response.*;
import com.unisinsight.business.model.PracticePersonDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 实习记录关联人员
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/14
 * @since 1.0
 */
public interface PracticePersonMapper extends Mapper<PracticePersonDO> {

    /**
     * 根据主键查找
     *
     * @param id 主键
     * @return 实习人员记录详情
     */
    PracticePersonDetailDTO findById(@Param("id") Integer id);

    /**
     * 条件查询
     *
     * @param req 参数
     * @return 记录列表
     */
    List<PracticePersonListDTO> findByConditions(PracticePersonQueryReqDTO req);

    /**
     * 根据条件统计导出数量
     *
     * @param req 查询参数
     * @return 总数
     */
    int countByConditions(PracticePersonQueryReqDTO req);

    /**
     * 导出实习统计
     *
     * @param req     查询参数
     * @param handler 流式导出回调
     */
    void exportByConditions(PracticePersonQueryReqDTO req, ResultHandler<PracticePersonListDTO> handler);

    /**
     * 查找实习申请关联的人员列表
     *
     * @param recordId 实习记录ID
     * @return 人员信息列表
     */
    List<PersonBaseInfoDTO> findPersonsByRecordId(@Param("recordId") Integer recordId);

    /**
     * 查找实习申请记录所包含的人员姓名
     *
     * @param recordIds 实习申请记录列表
     * @return 人员姓名列表，例：张三、王五、李六
     */
    List<DictBO> findPersonNameByRecordIds(@Param("recordIds") List<Integer> recordIds);

    /**
     * 查找实习申请的第一个人员的url
     *
     * @param recordIds 实习申请记录列表
     * @return 实习记录和人员url关联
     */
    List<DictBO> findFirstPersonUrl(@Param("recordIds") List<Integer> recordIds);

    /**
     * 查找实习申请关联的人员列表
     *
     * @param recordId 申诉记录
     * @return 人员编号列表
     */
    List<String> findPersonNosOfRecordId(@Param("recordId") Integer recordId);

    /**
     * 筛选当天处于实习的人员编号集合
     */
    Set<String> findPracticePersons(@Param("personNos") List<String> personNos,
                                    @Param("date") LocalDate date);

    /**
     * 查找当天有点名任务的人员
     *
     * @param personNos         人员变化集合
     * @param attendanceDate    考勤日期时间
     * @param attendanceEndDate 考勤结束日期
     */
    List<PracticeAttendancePersonBO> findHaveTaskPersons(@Param("personNos") Set<String> personNos,
                                                         @Param("attendanceDate") LocalDate attendanceDate,
                                                         @Param("attendanceEndDate") LocalDate attendanceEndDate);

    /**
     * 更新实习点名考勤结果
     */
    void updateAttendanceResults(@Param("results") List<PracticeAttendanceResultBO> results);

    /**
     * 更新实习点名缺勤记录
     */
    void updateAbsenceResults(@Param("recordIds") List<Integer> recordIds,
                              @Param("attendanceTime") LocalDateTime attendanceTime);

    /**
     * WEB 实习点名任务详情查询
     */
    List<PracticeAttendanceDetailDTO> findAttendanceDetails(PracticeAttendanceDetailsReqDTO param);

    /**
     * H5 实习点名列表查询
     */
    List<PracticeAttendancePersonListDTO> findAttendancePersons(PracticeAttendancePersonListReqDTO param);

    /**
     * H5 实习点名详情
     */
    PracticeAttendancePersonDetailsDTO findPersonAttendanceDetails(PracticeAttendancePersonDetailsReqDTO param);

    /**
     * H5 获取实习点名缺勤人数
     */
    Integer getAbsenceCount(@Param("userCode") String userCode);

    /**
     * H5 标记缺勤消息已读
     */
    void markAsRead(@Param("id") Integer id);

    /**
     * 根据身份证和日期查询请假
     */
    Integer selectByPersonNoAndDate(String personNo,LocalDate date);
}