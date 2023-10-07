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
public class SpotCheckCountExcelDTO {

    @ExcelProperty(value = {"序号"}, index = 0)
    @ColumnWidth(10)
    private Integer id;

    @ExcelProperty(value = {"学校姓名"}, index = 1)
    @ColumnWidth(10)
    private String schoolName;

    @ExcelProperty(value = {"所属组织"}, index = 2)
    @ColumnWidth(10)
    private String indexPathName;

    @ExcelProperty(value = {"考勤日期"}, index = 3)
    @ColumnWidth(20)
    private String attentDate;

    @ExcelProperty(value = {"抽查学生总数"}, index = 4)
    @ColumnWidth(10)
    private Integer stuTotal;

    @ExcelProperty(value = {"缺勤率"}, index = 5)
    @ColumnWidth(10)
    private String absenceRate;

    @ExcelProperty(value = {"缺勤学生"}, index = 6)
    @ColumnWidth(10)
    private Integer absenceNum;

    @ExcelProperty(value = {"在校学生"}, index = 7)
    @ColumnWidth(10)
    private Integer inSchoolNum;

    @ExcelProperty(value = {"请假学生"}, index = 8)
    @ColumnWidth(10)
    private Integer leaveNum;

    @ExcelProperty(value = {"实习学生"}, index = 9)
    @ColumnWidth(10)
    private Integer practiceNum;

}