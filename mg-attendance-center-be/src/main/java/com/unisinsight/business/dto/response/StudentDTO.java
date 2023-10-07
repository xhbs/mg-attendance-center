package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/17
 */
@Data
public class StudentDTO {
    @ApiModelProperty(value = "人员编号")
    private String personNo;
    @ApiModelProperty(value = "人员姓名")
    private String personName;
    @ApiModelProperty(value = "人员图片")
    private String personUrl;
    @ApiModelProperty(value = "相关描述")
    private String describe;
}
