package com.unisinsight.business.dto.request;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class DailyAttendanceHighLvlStaticExcelDTO {
    @ExcelProperty(value = {"序号"}, index = 0)
    @ColumnWidth(10)
    private Integer id;

    @ExcelProperty(value = {"考勤对象"}, index = 1)
    @ColumnWidth(10)
    private String orgName;

    @ExcelProperty(value = {"学年"}, index = 2)
    @ColumnWidth(10)
    private String schoolYear;

    @ExcelProperty(value = {"学期"}, index = 3)
    @ColumnWidth(10)
    private String schoolTerm;

    @ExcelProperty(value = {"月份"}, index = 4)
    @ColumnWidth(10)
    private String yearMonth;

    @ExcelProperty(value = {"类型"}, index = 5)
    @ColumnWidth(10)
    private String checkType = "周类型";

    @ExcelProperty(value = {"考勤周期"}, index = 6)
    @ColumnWidth(10)
    private Short checkWeek;

    @ExcelProperty(value = {"在籍总数"}, index = 7)
    @ColumnWidth(10)
    private Integer registStudentNum;


    @ExcelProperty(value = {"最新一期人数"}, index = 8)
    @ColumnWidth(10)
    private Integer studentNum;

    @ExcelProperty(value = {"在校率100%"}, index = 9)
    @ColumnWidth(10)
    private Integer range1;

    @ExcelProperty(value = {"在校率90-99%"}, index = 10)
    @ColumnWidth(10)
    private Integer range2;

    @ExcelProperty(value = {"在校率70-89%"}, index = 11)
    @ColumnWidth(10)
    private Integer range3;

    @ExcelProperty(value = {"在校率50-69%"}, index = 12)
    @ColumnWidth(10)
    private Integer range4;

    @ExcelProperty(value = {"在校率30-49%"}, index = 13)
    @ColumnWidth(10)
    private Integer range5;

    @ExcelProperty(value = {"在校率小于30%"}, index =14)
    @ColumnWidth(10)
    private Integer range6;



}