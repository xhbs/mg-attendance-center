package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实习点名 学生点名详情
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@Data
public class PracticeAttendancePersonListDTO {
    @ApiModelProperty(value = "实习申请记录ID", required = true)
    private Integer recordId;

    @ApiModelProperty(value = "人员编码", required = true)
    private String personNo;

    @ApiModelProperty(value = "人员名称", required = true)
    private String personName;

    @ApiModelProperty(value = "人员图像", required = true)
    private String personUrl;

    @ApiModelProperty(value = "考勤结果，-1未点名 0正常 99缺勤")
    private Integer attendanceResult;

    @ApiModelProperty(value = "考勤点名开始日期, yyyy-MM-dd", required = true)
    private String attendanceStartDate;

    @ApiModelProperty(value = "考勤点名结束日期, yyyy-MM-dd", required = true)
    private String attendanceEndDate;

    @ApiModelProperty(value = "申请时间, yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedAt;
}
