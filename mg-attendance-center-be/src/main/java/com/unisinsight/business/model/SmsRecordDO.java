package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 短信发送记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/10
 */
@Data
@Table(name = "sms_records")
public class SmsRecordDO {
    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 手机号码
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 短信内容
     */
    @Column(name = "message")
    private String message;

    /**
     * 是否发送成功
     */
    @Column(name = "success")
    private Boolean success;

    /**
     * 发送时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}