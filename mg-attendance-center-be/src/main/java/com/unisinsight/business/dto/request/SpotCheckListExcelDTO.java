package com.unisinsight.business.dto.request;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@ExcelIgnoreUnannotated
@Builder
public class SpotCheckListExcelDTO {

    @ExcelProperty(value = {"序号"}, index = 0)
    @ColumnWidth(10)
    private Integer id;

    @ExcelProperty(value = {"考勤状态"}, index = 1)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = {"学生姓名"}, index = 2)
    @ColumnWidth(10)
    private String stuName;

    @ExcelProperty(value = {"学号"}, index = 3)
    @ColumnWidth(10)
    private String stuNo;

    @ExcelProperty(value = {"班级"}, index = 4)
    @ColumnWidth(20)
    private String className;
}