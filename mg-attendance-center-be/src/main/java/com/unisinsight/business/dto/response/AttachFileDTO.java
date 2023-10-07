package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 附件文件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@Data
public class AttachFileDTO {
    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;
}