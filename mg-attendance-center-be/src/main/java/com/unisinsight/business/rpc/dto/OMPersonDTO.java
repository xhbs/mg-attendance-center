package com.unisinsight.business.rpc.dto;

import lombok.Data;

import java.util.Date;

/**
 * 对象管理 人员信息
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/4/12
 * @since 1.0
 */
@Data
public class OMPersonDTO {

    private String id;

    private String orgIndex;

    private String userCode;

    private String userImage;

    private String userName;

    private String className;

    private Short isRecord;

    private Short isSchool;
}
