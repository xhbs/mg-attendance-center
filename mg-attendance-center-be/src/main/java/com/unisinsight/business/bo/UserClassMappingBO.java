package com.unisinsight.business.bo;

import lombok.Data;

/**
 * 班主任和班级绑定对象
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/1/21
 * @since 1.0
 */
@Data
public class UserClassMappingBO {
    private String orgIndex;
    private String userName;
    private String cellPhone;
}
