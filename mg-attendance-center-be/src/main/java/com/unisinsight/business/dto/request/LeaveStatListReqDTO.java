package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PaginationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 请假统计分页查询 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LeaveStatListReqDTO extends PaginationReq {

    @ApiModelProperty(value = "组织索引", required = true)
    private String orgIndexPath;

    @ApiModelProperty(value = "班级组织编码")
    private String classOrgIndex;

    @ApiModelProperty(value = "请假状态, 1请假待生效 2请假中 3请假结束")
    private Integer leaveState;

    @ApiModelProperty(value = "请假类型, 1：病假，2：事假，3：实习登记，99：其他", required = true)
    private Integer type;

    @ApiModelProperty(value = "开始日期, yyyy-MM-dd")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期, yyyy-MM-dd")
    private LocalDate endDate;

    @ApiModelProperty(value = "模糊搜索，学生姓名/学号")
    private String search;

    @ApiModelProperty(value = "请假状态倒序查询")
    private Boolean orderByStateDesc;
}
