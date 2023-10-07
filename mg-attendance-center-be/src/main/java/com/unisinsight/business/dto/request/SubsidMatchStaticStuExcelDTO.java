package com.unisinsight.business.dto.request;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("资助比对学生列表")
public class SubsidMatchStaticStuExcelDTO {

    @ExcelProperty(value = {"比对状态"}, index = 0)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = {"学生姓名"}, index = 1)
    @ColumnWidth(10)
    private String personName;

    @ExcelProperty(value = {"班级"}, index = 2)
    @ColumnWidth(10)
    private String orgName;

    @ExcelProperty(value = {"学号"}, index = 3)
    @ColumnWidth(10)
    private String personNo;

    @ExcelProperty(value = {"考勤时段"}, index = 4)
    @ColumnWidth(10)
    private String chkDateRange;

    @ExcelProperty(value = {"考勤周期"}, index = 5)
    @ColumnWidth(10)
    private Integer totalNum;

    @ExcelProperty(value = {"缺勤率(%)"}, index = 6)
    @ColumnWidth(10)
    private Integer absentRate;

    @ExcelProperty(value = {"正常次数"}, index = 7)
    @ColumnWidth(10)
    private Integer normalNum;

    @ExcelProperty(value = {"缺勤次数"}, index = 8)
    @ColumnWidth(10)
    private Integer absentNum;

}