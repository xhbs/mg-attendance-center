package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tanggang
 * @version 1.0
 * @email tang.gang@inisinsight.com
 * @date 2021/9/1 16:20
 **/
@Data
public class SubsidRosterInfo {
    @ApiModelProperty(value = "名单日期,yyyy-MM",required = true)
    private String rosterDate;

    @ApiModelProperty(value = "项目类型，0-免学费，1-国家助学金",required = true)
    private Short prjType;
}
