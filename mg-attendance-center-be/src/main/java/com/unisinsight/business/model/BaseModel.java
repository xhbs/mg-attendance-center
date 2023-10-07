package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 公共字段
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/7/30
 */
@Data
public class BaseModel {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 创建人编号
     */
    @Column(name = "created_by")
    private String createdBy;
}
