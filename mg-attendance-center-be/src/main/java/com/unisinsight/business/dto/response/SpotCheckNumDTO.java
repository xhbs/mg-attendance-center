package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 抽查考勤明细查询 出参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/9/10
 */
@Data
public class SpotCheckNumDTO {

    @ApiModelProperty(name = "in_progress_num", value = "进行中的数量", required = true, example = "4")
    private Integer inProgressNum;

    @ApiModelProperty(name = "not_performed_num", value = "未进行的数量", required = true, example = "2")
    private Integer notPerformedNum;
}
