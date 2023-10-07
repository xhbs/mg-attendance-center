package com.unisinsight.business.dto.request;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
@Builder
public class DailyAttendanceStuStaticExcelDTO {

    @ExcelProperty(value = {"序号"}, index = 0)
    @ColumnWidth(10)
    private Integer id;

    @ExcelProperty(value = {"学生姓名"}, index = 1)
    @ColumnWidth(10)
    private String personName;

    @ExcelProperty(value = {"学号"}, index = 2)
    @ColumnWidth(10)
    private String personNo;

    @ExcelProperty(value = {"学年"}, index = 3)
    @ColumnWidth(20)
    private String schoolYear;

    @ExcelProperty(value = {"学期"}, index = 4)
    @ColumnWidth(10)
    private String schoolTerm;

    @ExcelProperty(value = {"月份"}, index = 5)
    @ColumnWidth(10)
    private String yearMonth;

    @ExcelProperty(value = {"类型"}, index = 6)
    @ColumnWidth(10)
    private String checkType = "周类型";

    @ExcelProperty(value = {"考勤周期"}, index = 7)
    @ColumnWidth(10)
    private Short checkWeek;

    @ExcelProperty(value = {"在校"}, index = 8)
    @ColumnWidth(10)
    private Integer normalWeeks;

    @ExcelProperty(value = {"缺勤"}, index = 9)
    @ColumnWidth(10)
    private Integer absentWeeks;


}