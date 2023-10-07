package com.unisinsight.business.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
@Builder
public class SubsidStuAttendanceResultsExcelDTO {

    @ExcelProperty(value = {"序号"}, index = 0)
    @ColumnWidth(10)
    private Integer id;

    @ExcelProperty(value = {"状态：0在校 99缺勤"}, index = 1)
    @ColumnWidth(10)
    private String result;

    @ExcelProperty(value = {"考勤名称"}, index = 2)
    @ColumnWidth(10)
    private String taskName;

    @ExcelProperty(value = {"考勤类型"}, index = 3)
    @ColumnWidth(10)
    private String attendanceType;

    @ExcelProperty(value = {"考勤日期"}, index = 4)
    @ColumnWidth(10)
    private String attendanceRangeDate;

    @ExcelProperty(value = {"周一考勤结果"}, index = 5)
    @ColumnWidth(10)
    private String resultOfMonday;

    @ExcelProperty(value = {"周二考勤结果"}, index = 6)
    @ColumnWidth(10)
    private String resultOfTuesday;

    @ExcelProperty(value = {"周三考勤结果"}, index = 7)
    @ColumnWidth(10)
    private String resultOfWednesday;

    @ExcelProperty(value = {"周四考勤结果"}, index = 8)
    @ColumnWidth(10)
    private String resultOfThursday;

    @ExcelProperty(value = {"周五考勤结果"}, index = 9)
    @ColumnWidth(10)
    private String resultOfFriday;

}