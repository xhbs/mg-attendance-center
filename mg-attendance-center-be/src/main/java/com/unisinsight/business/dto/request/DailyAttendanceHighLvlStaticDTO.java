package com.unisinsight.business.dto.request;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
public class DailyAttendanceHighLvlStaticDTO {
    @ApiModelProperty(value = "记录id")
    @ExcelProperty(value = {"序号"}, index = 0)
    @ColumnWidth(10)
    private Long id;

    @ApiModelProperty( value = "组织类型，0：根节点；1：省；2：市/州;3:区/县；4：学校；5：班级",required = true)
    @ExcelProperty(value = {"组织类型"}, index = 1)
    @ColumnWidth(10)
    private Integer subType;

    @ApiModelProperty(value = "父组织节点")
    @ExcelProperty(value = {"父组织节点"}, index = 2)
    @ColumnWidth(10)
    private String orgParentIndex;

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
    @ColumnWidth(10)
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

    @ApiModelProperty(value = "在籍学生总数")
    @ExcelProperty(value = {"在籍学生总数"}, index = 9)
    @ColumnWidth(10)
    private Integer registStudentNum;

    @ApiModelProperty(value = "注册总数")
    @ExcelProperty(value = {"注册总数"}, index = 9)
    @ColumnWidth(10)
    private Integer studentNum;

    @ApiModelProperty(value = "人数(出席率100%)")
    @ExcelProperty(value = {"出席率100%"}, index = 10)
    @ColumnWidth(10)
    private Integer range1;

    @ApiModelProperty(value = "人数(出席率90-99%)")
    @ExcelProperty(value = {"出席率90-99%"}, index = 11)
    @ColumnWidth(10)
    private Integer range2;

    @ApiModelProperty(value = "人数(出席率70-89%)")
    @ExcelProperty(value = {"出席率70-89%"}, index = 12)
    @ColumnWidth(10)
    private Integer range3;

    @ApiModelProperty(value = "人数(出席率50-69%)")
    @ExcelProperty(value = {"出席率50-69%"}, index = 13)
    @ColumnWidth(10)
    private Integer range4;

    @ApiModelProperty(value = "人数(出席率30-49%)")
    @ExcelProperty(value = {"出席率30-49%"}, index = 14)
    @ColumnWidth(10)
    private Integer range5;

    @ApiModelProperty(value = "人数(出席率小于30%)")
    @ExcelProperty(value = {"出席率小于30%"}, index =15)
    @ColumnWidth(10)
    private Integer range6;

    @ApiModelProperty(value = "暂未使用")
    private Integer range7;

    @ApiModelProperty(value = "暂未使用")
    private Integer range8;


}