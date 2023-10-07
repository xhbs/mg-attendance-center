package com.unisinsight.business.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 误报信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/28
 * @since 1.0
 */
@Data
public class MisreportInfo {

    @ApiModelProperty("核实位置")
    private String checkLocation;

    @ApiModelProperty("位置经度")
    private String locationLongitude;

    @ApiModelProperty("位置纬度")
    private String locationLatitude;

    @ApiModelProperty("核实结果")
    private String checkResult;

    @ApiModelProperty("比对图片url，fms相对路径")
    private String matchImage;

    @ApiModelProperty("比对结果，0失败 1成功")
    private Short matchResult;

    @ApiModelProperty("备注")
    private String comment;
}
