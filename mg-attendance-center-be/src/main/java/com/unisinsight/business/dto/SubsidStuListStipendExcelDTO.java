package com.unisinsight.business.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
public class SubsidStuListStipendExcelDTO extends SubsidStuListBaseExcelDTO {

    @ExcelProperty(value = {"学生姓名"}, index = 0)
    @ColumnWidth(10)
    private String personName;

    @ExcelProperty(value = {"学籍号"}, index = 1)
    @ColumnWidth(10)
    private String schoolRollNo;

    @ExcelProperty(value = {"证件类型"}, index = 2)
    @ColumnWidth(10)
    private String certType;

    @ExcelProperty(value = {"证件号码"}, index = 3)
    @ColumnWidth(10)
    private String certNo;

    @ExcelProperty(value = {"性别"}, index = 4)
    @ColumnWidth(10)
    private String gender;

    @ExcelProperty(value = {"出生日期"}, index = 5)
    @ColumnWidth(10)
    private String birthDay;

    @ExcelProperty(value = {"年级"}, index = 6)
    @ColumnWidth(10)
    private String gradeName;

    @ExcelProperty(value = {"班级名称"}, index = 7)
    @ColumnWidth(10)
    private String className;

    @ExcelProperty(value = {"申请理由名称"}, index = 8)
    @ColumnWidth(10)
    private String aplyReason;

    @ExcelProperty(value = {"年份"}, index = 9)
    @ColumnWidth(10)
    private String year;

    @ExcelProperty(value = {"月份"}, index = 10)
    @ColumnWidth(10)
    private String month;

    @ExcelProperty(value = {"资助档次"}, index = 11)
    @ColumnWidth(10)
    private String subsidGrade;

    @ExcelProperty(value = {"应发金额"}, index = 12)
    @ColumnWidth(10)
    private String payAmt;

    @ExcelProperty(value = {"申请理由描述"}, index = 13)
    @ColumnWidth(10)
    private String aplyResonDesc;

    @ExcelProperty(value = {"专业"}, index = 14)
    @ColumnWidth(10)
    private String major;

    @ExcelProperty(value = {"学号"}, index = 15)
    @ColumnWidth(10)
    private String studentNo;

    @ExcelProperty(value = {"入学时间"}, index = 16)
    @ColumnWidth(10)
    private String admissionDate;

    @ExcelProperty(value = {"银行卡号"}, index = 17)
    @ColumnWidth(10)
    private String bankNo;

}