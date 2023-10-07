package com.unisinsight.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * 学生信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDTO {
    /**
     * 人员编码
     */
    @ApiModelProperty(value = "人员编码", required = true, example = "10033")
    private String personNo;

    /**
     * 人员名称
     */
    @ApiModelProperty(value = "人员名称", required = true, example = "田七")
    private String personName;

    /**
     * 人员图像
     */
    @ApiModelProperty(value = "人员图像", required = true, example = "/file/mg/xxx.jpg")
    private String personUrl;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别: 1男 2女", required = true, example = "1")
    private Integer gender;

    /**
     * 入学时间
     */
    @ApiModelProperty(value = "入学时间, yyyy-MM-dd", example = "2019-09-01")
    private String admissionDate;

    /**
     * 所属组织
     */
    @ApiModelProperty(value = "所属组织名称", required = true, example = "大一1班<xx学校<xx县<xx市")
    private String orgIndexPathName;



    @ApiModelProperty(value = "所属组织编码")
    private String orgIndex;
    private String schoolOrgIndex;

    private String tabName;
}
