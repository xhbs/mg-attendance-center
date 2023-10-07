package com.unisinsight.business.service;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.AttendWeekResultDTO;
import com.unisinsight.business.dto.request.CallTheRollDto;
import com.unisinsight.business.dto.request.QueryStudentListDto;
import com.unisinsight.business.dto.request.UpdateAttendDto;
import com.unisinsight.business.dto.response.*;

import java.util.List;

/**
 * 日常考勤
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/16
 */
public interface DailyAttendanceService {

    /**
     * 学生数量统计
     */
    DailyAttendanceDTO countStudents();

    /**
     * 查询在籍学生
     */
    PaginationRes<StudentDTO> queryStudent(QueryStudentListDto req);

    /**
     * 查询在校学生
     */
    PaginationRes<AttendWeekResultDTO> queryInSchool(QueryStudentListDto req);

    /**
     * 查询学生考勤记录
     */
    List<InSchoolStuDTO> queryAttend(String personNo);

    /**
     * 查询请假中学生
     */
    PaginationRes<LeaveStudentDTO> queryLeave(QueryStudentListDto req);

    /**
     * 查询实习中学生
     */
    PaginationRes<PracticeStudentDTO> queryPractice(QueryStudentListDto req);

    /**
     * 更新考勤结果
     */
    void updateAttend(CallTheRollDto req);
}
