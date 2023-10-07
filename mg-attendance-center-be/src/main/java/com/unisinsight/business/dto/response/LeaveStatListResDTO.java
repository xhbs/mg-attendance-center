package com.unisinsight.business.dto.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unisinsight.business.common.enums.LeaveState;
import com.unisinsight.business.common.enums.LeaveType;
import com.unisinsight.business.dto.ExportConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 请假统计分页查询 出参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/23
 */
@Data
public class LeaveStatListResDTO implements ExportConvert {

    @ApiModelProperty(value = "请假记录ID")
    @ExcelIgnore
    private Integer id;

    @ApiModelProperty(value = "请假状态, 0请假待生效 1 请假中 2请假结束")
    @ExcelIgnore
    private Integer leaveState;

    @ApiModelProperty(value = "请假类型, 1：病假，2：事假，3：实习登记，99：其他", required = true)
    @ExcelIgnore
    private Integer type;

    @ApiModelProperty(value = "学号")
    @ExcelProperty(value = "学号", index = 3)
    private String personNo;

    @ApiModelProperty(value = "学生姓名")
    @ExcelProperty(value = "学生姓名", index = 2)
    private String personName;

    @ApiModelProperty(value = "请假时段-开始日期, yyyy-MM-dd")
    @ExcelIgnore
    private String startDate;

    @ApiModelProperty(value = "请假时段-结束日期, yyyy-MM-dd")
    @ExcelIgnore
    private String endDate;

    @ApiModelProperty(value = "班级名称")
    @ExcelProperty(value = "班级", index = 6)
    private String orgName;

    @ApiModelProperty(value = "学校名称")
    @ExcelProperty(value = "学校", index = 5)
    private String schoolName;

    @ApiModelProperty(value = "上报人姓名")
    @ExcelProperty(value = "上报人", index = 7)
    private String creatorName;

    @ApiModelProperty(value = "审核人姓名")
    @ExcelProperty(value = "审核人", index = 8)
    private String approvedBy;

    // ---------- excel导出字段 ----------

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "序号", index = 0)
    @JsonIgnore
    private Integer serialNo;

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "请假状态", index = 1)
    @JsonIgnore
    private String leaveStateText;

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "请假类型", index = 4)
    @JsonIgnore
    private String typeText;

    @Override
    public void convertToExcel(int serialNo) {
        this.serialNo = serialNo + 1;
        if (leaveState != null) {
            leaveStateText = LeaveState.getNameOfValue(leaveState);
        }
        typeText = LeaveType.getNameOfValue(type);
    }
}
