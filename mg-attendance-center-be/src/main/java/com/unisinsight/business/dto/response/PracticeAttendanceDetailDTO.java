package com.unisinsight.business.dto.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unisinsight.business.common.enums.AttendanceResult;
import com.unisinsight.business.dto.ExportConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 实习点名分页查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/13
 */
@Data
public class PracticeAttendanceDetailDTO implements ExportConvert {
    @ApiModelProperty(value = "考勤状态, -1暂未考勤 0考勤正常 99考勤缺勤", required = true)
    @ExcelIgnore
    private Integer attendanceResult;

    @ApiModelProperty(value = "学号", required = true)
    @ExcelProperty(value = "学号", index = 3)
    private String personNo;

    @ApiModelProperty(value = "学生姓名", required = true)
    @ExcelProperty(value = "学生姓名", index = 2)
    private String personName;

    @ApiModelProperty(value = "班级", required = true)
    @ExcelProperty(value = "班级", index = 4)
    private String className;

    @ApiModelProperty(value = "考勤日期, yyyy-MM-dd")
    @ExcelProperty(value = "考勤日期", index = 5)
    private String attendanceDate;

    @ApiModelProperty(value = "考勤时间, yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "考勤时间", index = 6)
    private String attendanceTime;

    // ---------- excel导出字段 ----------

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "序号", index = 0)
    @JsonIgnore
    private Integer serialNo;

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "考勤状态", index = 1)
    @JsonIgnore
    private String attendanceResultText;

    @Override
    public void convertToExcel(int serialNo) {
        this.serialNo = serialNo + 1;
        if (attendanceResult != null) {
            if (attendanceResult == AttendanceResult.NORMAL.getType()) {
                this.attendanceResultText = "考勤正常";
            } else if (attendanceResult == AttendanceResult.ABSENCE.getType()) {
                this.attendanceResultText = "考勤缺勤";
            } else {
                this.attendanceResultText = "暂未考勤";
            }
        }
    }
}
