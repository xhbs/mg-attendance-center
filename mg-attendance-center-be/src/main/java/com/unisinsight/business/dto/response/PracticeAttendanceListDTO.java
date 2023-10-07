package com.unisinsight.business.dto.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PracticeAttendanceListDTO implements ExportConvert {

    @ApiModelProperty(value = "实习申请记录ID", required = true)
    @ExcelIgnore
    private Integer recordId;

    @ApiModelProperty(value = "考勤状态, 0进行中 1通过 2未通过", required = true)
    @ExcelIgnore
    private Integer attendanceState;

    @ApiModelProperty(value = "学生姓名", example = "张三、王五、李六", required = true)
    @ExcelProperty(value = "学生姓名", index = 2)
    private String personNames;

    @ApiModelProperty(value = "申请时间, yyyy-MM-dd HH:mm:ss", required = true)
    @ExcelProperty(value = "申请时间", index = 3)
    private String reportedAt;

    @ApiModelProperty(value = "考勤人数", required = true)
    @ExcelProperty(value = "考勤人数", index = 4)
    private Integer numOfPerson;

    @ApiModelProperty(value = "考勤正常人数", required = true)
    @ExcelProperty(value = "考勤正常", index = 5)
    private Integer numOfNormal;

    @ApiModelProperty(value = "考勤缺勤人数", required = true)
    @ExcelProperty(value = "考勤缺勤", index = 6)
    private Integer numOfAbsence;

    @ApiModelProperty(value = "未考勤人数", required = true)
    @ExcelProperty(value = "暂未考勤", index = 7)
    private Integer numOfNone;

    // ---------- excel导出字段 ----------

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "序号", index = 0)
    @JsonIgnore
    private Integer serialNo;

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "考勤状态", index = 1)
    @JsonIgnore
    private String attendanceStateText;

    @Override
    public void convertToExcel(int serialNo) {
        this.serialNo = serialNo + 1;
        if (attendanceState != null) {
            if (attendanceState == 0) {
                this.attendanceStateText = "点名中";
            } else if (attendanceState == 1) {
                this.attendanceStateText = "点名通过";
            } else if (attendanceState == 2) {
                this.attendanceStateText = "点名未通过";
            }
        }
    }
}
