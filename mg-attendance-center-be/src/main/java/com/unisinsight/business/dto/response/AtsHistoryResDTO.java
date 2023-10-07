package com.unisinsight.business.dto.response;

import com.unisinsight.business.bo.HistoryItem;
import com.unisinsight.business.bo.HistoryStatistics;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 考勤历史
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/25
 * @since 1.0
 */
@Getter
@Setter
public class AtsHistoryResDTO {
    // /**
    //  * 人员编码
    //  */
    // @ApiModelProperty(value = "人员编码")
    // private String personNo;
    //
    // /**
    //  * 人员名称
    //  */
    // @ApiModelProperty(value = "人员名称")
    // private String personName;
    //
    // /**
    //  * 所属组织路径
    //  */
    // @ApiModelProperty(value = "所属组织路径")
    // private String indexPath;
    //
    // /**
    //  * 所属组织名称
    //  */
    // @ApiModelProperty(value = "所属组织名称")
    // private String indexPathName;

    /**
     * 考勤统计
     */
    @ApiModelProperty(value = "考勤统计")
    private List<HistoryStatistics> statistics;

    /**
     * 考勤分布
     */
    @ApiModelProperty(value = "考勤分布")
    private List<HistoryItem> records;
}
