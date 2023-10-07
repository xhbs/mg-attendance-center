package com.unisinsight.business.dto.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AppealStatListResDTO implements ExportConvert {

    @ApiModelProperty(value = "组织编码")
    @ExcelIgnore
    private String orgIndex;

    @ApiModelProperty(value = "组织名称")
    @ExcelProperty(value = "组织", index = 1)
    private String orgName;

    @ApiModelProperty(value = "组织类型")
    @ExcelIgnore
    private Short subType;

    @ApiModelProperty(value = "申诉总人数")
    @ExcelProperty(value = "申诉总人数", index = 2)
    private Integer total;

    @ApiModelProperty(value = "未处理人数")
    @ExcelProperty(value = "未处理人数", index = 3)
    private Integer notApprovalNum;

    @ApiModelProperty(value = "同意人数")
    @ExcelProperty(value = "同意人数", index = 4)
    private Integer passedNum;

    @ApiModelProperty(value = "拒绝人数")
    @ExcelProperty(value = "拒绝人数", index = 5)
    private Integer rejectedNum;

    // ---------- excel导出字段 ----------

    @ApiModelProperty(hidden = true)
    @ExcelProperty(value = "序号", index = 0)
    @JsonIgnore
    private Integer serialNo;

    @Override
    public void convertToExcel(int serialNo) {
        this.serialNo = serialNo + 1;
    }
}
