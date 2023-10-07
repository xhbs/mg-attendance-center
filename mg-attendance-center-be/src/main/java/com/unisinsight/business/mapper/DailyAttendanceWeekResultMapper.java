package com.unisinsight.business.mapper;

import com.unisinsight.business.bo.FindStuWeekResultParamBO;
import com.unisinsight.business.bo.PersonOfClassBO;
import com.unisinsight.business.bo.SubsidAttendanceResultBO;
import com.unisinsight.business.dto.*;
import com.unisinsight.business.dto.response.DailyAttendanceDTO;
import com.unisinsight.business.model.DailyAttendanceWeekResultDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 日常考勤结果表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/13
 */
public interface DailyAttendanceWeekResultMapper extends Mapper<DailyAttendanceWeekResultDO>,
        InsertUseGeneratedKeysMapper<DailyAttendanceWeekResultDO>, PartitionByTimeTableMapper {

    /**
     * 批量保存
     */
    void batchSave(@Param("results") List<DailyAttendanceWeekResultDO> results);

    /**
     * 更新
     */
    void updateResult(DailyAttendanceWeekResultDO result);

    /**
     * 批量更新
     */
    void batchUpdate(@Param("results") List<DailyAttendanceWeekResultDO> results);

    /**
     * 根据人员查找本周的记录
     */
    DailyAttendanceWeekResultDO findByPersonOfWeek(@Param("personNo") String personNo,
                                                   @Param("startDate") LocalDate startDate);

    /**
     * 根据人员查找本周的记录
     */
    List<DailyAttendanceWeekResultDO> findByPersonsOfWeek(@Param("personNos") List<String> personNos,
                                                          @Param("startDate") LocalDate startDate);

    /**
     * 查询本周缺勤的人员
     */
    List<PersonOfClassBO> findAbsencePersonsAtWeek(@Param("monday") LocalDate monday);

    /**
     * 分页查询
     */
    List<DailyAttendanceWeekResultDO> query(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("personNo") String personNo,
                                            @Param("result") Integer result,
                                            @Param("orderByResultDesc") Boolean orderByResultDesc);

    /**
     * 根据班级查询学生考勤结果
     */
    List<AttendWeekResultDTO> findByClasses(@Param("classOrgIndexes") List<String> classOrgIndexes,
                                            @Param("date") LocalDate date,
                                            @Param("result") Integer result,
                                            @Param("searchKey") String searchKey);

    /**
     * 统计学生在周期内出勤周数
     */
    List<StuWeekResultCountDTO> findStuWeekResultList(FindStuWeekResultParamBO param);

    List<SubsidAttendanceResultBO> findStuWeekAttendanceResultList(FindStuWeekResultParamBO param);

    /**
     * 日常考勤学生数量统计
     *
     * @param orgIndexes 班级的组织编码集合
     * @param startDate  周考勤开始日期
     * @param endDate    周考勤结束日期
     * @return 统计结果
     */
    DailyAttendanceDTO countStudents(@Param("orgIndexes") List<String> orgIndexes,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    /**
     * 根据父级组织id查询学校考勤统计
     *
     * @param suffix
     * @param parents
     * @return
     */
    StatisticsProvince statisticsByParents(@Param("suffix") String suffix, @Param("parents") List<String> parents);

    /**
     * 各地州学校考勤统计
     *
     * @param suffix
     * @param parents
     * @return
     */
    StatisticsProvince statisticsNotInParents(@Param("suffix") String suffix, @Param("parents") List<String> parents);

    /**
     * 学校数量统计
     *
     * @param suffix
     * @return
     */
    List<StatisticsAllSchool> allSchoolStatistics(@Param("suffix") String suffix);

    /**
     * 根据学校id集合统计
     * @param suffix
     * @param schoolIds
     * @return
     */
    List<StatisticsAllSchool> statisticsBySchoolIds(@Param("suffix") String suffix, List<String> schoolIds);

    /**
     * 根据学校id统计班级
     * @param suffix
     * @param schoolId
     * @return
     */
    List<StatisticsAllSchool> classStatistics(@Param("suffix") String suffix, String schoolId);

}
