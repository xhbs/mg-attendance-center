package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 通用审批 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@Data
public class ApprovalReqDTO {
    @ApiModelProperty(value = "ID列表", required = true)
    @NotEmpty
    private List<Long> ids;

    @ApiModelProperty(value = "状态: 1 同意，2 拒绝 ", required = true)
    @NotNull
    private Integer result;

    @ApiModelProperty(value = "处理备注")
    @Length(max = 500)
    private String comment;
}
