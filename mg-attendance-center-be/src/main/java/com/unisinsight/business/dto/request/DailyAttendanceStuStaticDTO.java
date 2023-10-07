package com.unisinsight.business.dto.request;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
public class DailyAttendanceStuStaticDTO {

    @ApiModelProperty(value = "记录id")
    @ExcelProperty(value = {"序号"}, index = 0)
    @ColumnWidth(10)
    private Long id;

    @ApiModelProperty(value = "学生编号")
    @ExcelProperty(value = {"学生编号"}, index = 1)
    @ColumnWidth(10)
    private String personNo;

    @ApiModelProperty(value = "学生姓名")
    @ExcelProperty(value = {"学生姓名"}, index = 2)
    @ColumnWidth(10)
    private String personName;


    @ApiModelProperty(value = "组织标识")
    @ExcelProperty(value = {"组织标识"}, index = 3)
    @ColumnWidth(10)
    private String orgIndex;

    @ApiModelProperty(value = "组织名称")
    @ExcelProperty(value = {"组织名称"}, index = 4)
    @ColumnWidth(10)
    private String orgName;

    @ApiModelProperty(value = "学年(格式：2020-2021)")
    @ExcelProperty(value = {"学年"}, index = 5)
    @ColumnWidth(20)
    private String schoolYear;

    @ApiModelProperty(value = "学期（0-春季，1-秋季）")
    @ExcelProperty(value = {"学期"}, index = 6)
    @ColumnWidth(10)
    private String schoolTerm;

    @ApiModelProperty(value = "月份（yyyy-mm格式）")
    @ExcelProperty(value = {"月份"}, index = 7)
    @ColumnWidth(10)
    private String yearMonth;

    @ApiModelProperty(value = "考勤周期")
    @ExcelProperty(value = {"考勤周期"}, index = 8)
    @ColumnWidth(10)
    private Short checkWeek;

    @ApiModelProperty(value = "正常出席周数")
    @ExcelProperty(value = {"正常出席周数"}, index = 9)
    @ColumnWidth(10)
    private Integer normalWeeks;

    @ApiModelProperty(value = "缺席周数")
    @ExcelProperty(value = {"缺席周数"}, index = 10)
    @ColumnWidth(10)
    private Integer absentWeeks;


}