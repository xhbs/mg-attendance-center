package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
@Table(name = "user_info")
public class UserInfoDO {

    /**
     * 账号
     */
    @Column(name = "user_code")
    private String userCode;

    /**
     * 密码
     */
    @Column(name = "password")
    private String password;

}
