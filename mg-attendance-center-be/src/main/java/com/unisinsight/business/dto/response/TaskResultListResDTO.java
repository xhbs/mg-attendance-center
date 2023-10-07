package com.unisinsight.business.dto.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 抽查考勤明细查询 出参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
public class TaskResultListResDTO {

    @ApiModelProperty(name = "result", value = "考勤结果 0正常 2实习 3请假 4申诉通过 99缺勤", required = true, example = "0")
    @ExcelProperty
    private Integer result;

    @ApiModelProperty(name = "person_no", value = "学号", required = true, example = "F1B98D82BBB6356A6460BA73F86C9C96")
    private String personNo;

    @ApiModelProperty(name = "person_name", value = "姓名", required = true, example = "张三")
    private String personName;

    @ApiModelProperty(name = "class_name", value = "班级名称", required = true, example = "大一1班")
    private String className;
}
