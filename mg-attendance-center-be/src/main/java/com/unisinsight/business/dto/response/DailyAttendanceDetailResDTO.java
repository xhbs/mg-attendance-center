package com.unisinsight.business.dto.response;

import com.unisinsight.business.dto.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 考勤明细详情 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/16
 */
@Data
public class DailyAttendanceDetailResDTO {

    @ApiModelProperty(name = "result", value = "考勤结果：0在校 2实习 3请假 99缺勤", required = true)
    private Integer result;

    @ApiModelProperty(name = "attendance_date", value = "考勤日期, yyyy-MM-dd", required = true)
    private String attendanceDate;

    @ApiModelProperty(name = "attendance_time", value = "考勤时间, yyyy-MM-dd HH:mm:ss", required = true)
    private String attendanceTime;

    @ApiModelProperty(name = "person_info", value = "学生信息", required = true)
    private PersonDTO personInfo;

    @ApiModelProperty(name = "leave_info", value = "请假信息")
    private LeaveInfoDTO leaveInfo;

    @ApiModelProperty(name = "practice_info", value = "实习信息")
    private PracticeInfoDTO practiceInfo;

    @ApiModelProperty(name = "recent_records", value = "最近抓拍记录")
    private List<CaptureRecordDTO> recentRecords;

    @ApiModelProperty(name = "change_records", value = "考勤状态更改记录")
    private List<ResultChangeRecordDTO> changeRecords;
}
