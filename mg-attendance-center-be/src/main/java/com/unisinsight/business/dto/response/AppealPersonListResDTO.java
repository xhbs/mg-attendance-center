package com.unisinsight.business.dto.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unisinsight.business.common.enums.ApprovalStatus;
import com.unisinsight.business.dto.ExportConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 申诉统计分页查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/23
 */
@Data
public class AppealPersonListResDTO implements ExportConvert {

    @ApiModelProperty(value = "学号")
    @ExcelProperty(value = "学号", index = 3)
    private String personNo;

    @ApiModelProperty(value = "姓名")
    @ExcelProperty(value = "姓名", index = 2)
    private String personName;

    @ApiModelProperty(value = "班级")
    @ExcelProperty(value = "班级", index = 4)
    private String className;

    @ApiModelProperty(value = "巡更记录ID")
    @ExcelIgnore
    private Integer appealRecordId;

    @ApiModelProperty(value = "审批状态： 1未上报 2审批中 3同意 4拒绝")
    @ExcelIgnore
    private Integer status;

    @ApiModelProperty(value = "考勤时段", example = "2021-08-01 ～ 2021-08-05")
    @ExcelProperty(value = "考勤时段", index = 5)
    private String attendanceDateRange;

    @ApiModelProperty(value = "申诉时间", example = "2021-08-11 12:00:12")
    @ExcelProperty(value = "申诉时间", index = 6)
    private String reportedAt;

    // ---------- excel导出字段 ----------

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "序号", index = 0)
    @JsonIgnore
    private Integer serialNo;

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "申诉状态", index = 1)
    @JsonIgnore
    private String statusText;

    @Override
    public void convertToExcel(int serialNo) {
        this.serialNo = serialNo + 1;
        if (status != null) {
            statusText = ApprovalStatus.getNameOfValue(status);
        }
    }
}
