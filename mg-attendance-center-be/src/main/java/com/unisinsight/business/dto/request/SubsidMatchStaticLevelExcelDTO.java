package com.unisinsight.business.dto.request;

import cn.hutool.core.annotation.Alias;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;


@Data
public class SubsidMatchStaticLevelExcelDTO {

    @ExcelProperty(value = {"资助单位"}, index = 0)
    @ColumnWidth(10)
    @Alias("资助单位")
    private String orgName;

    @ExcelProperty(value = {"考勤时段"}, index = 1)
    @ColumnWidth(10)
    @Alias("考勤时段")
    private String chkDateRange;

    @ExcelProperty(value = {"在籍人数"}, index = 2)
    @ColumnWidth(10)
    @Alias("在籍人数")
    private Integer studentNum;

    @ExcelProperty(value = {"资助审核人数"}, index = 3)
    @ColumnWidth(10)
    @Alias("资助审核人数")
    private Integer subNum;

    @ExcelProperty(value = {"比对通过人数"}, index = 4)
    @ColumnWidth(10)
    @Alias("比对通过人数")
    private Integer matchPassNum;

    @ExcelProperty(value = {"比对不通过人数"}, index = 5)
    @ColumnWidth(10)
    @Alias("比对不通过人数")
    private Integer matchNoPassNum;

}