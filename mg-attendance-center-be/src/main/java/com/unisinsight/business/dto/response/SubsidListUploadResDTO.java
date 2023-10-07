package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Data;

/**
 * @author tanggang
 * @version 1.0
 * @description
 * @email tang.gang@inisinsight.com
 * @date 2021/9/27 19:34
 **/
@Data
@Builder
@ApiModel("上传资助比对返回对象")
public class SubsidListUploadResDTO {
    @ApiModelProperty(value = "返回码(0-上传失败，1-上传成功，2-部分成功)",example = "1",required = true)
    private Integer respCode;

    @ApiModelProperty(value = "描述",example = "张三，李四名单数据异常")
    private String message;

    @ApiModelProperty(value = "名单标识" )
    private String subListIndex;
}
