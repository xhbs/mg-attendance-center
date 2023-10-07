package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 系统配置
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/10/21
 * @since 1.0
 */
@Data
@Table(name = "system_configs")
public class SystemConfigDO {
    /**
     * 配置名称
     */
    @Id
    @Column(name = "key")
    private String key;

    /**
     * 配置值
     */
    @Column(name = "value")
    private String value;

    /**
     * 描述
     */
    @Column(name = "describe")
    private String describe;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 更新人
     */
    @Column(name = "updated_by")
    private String updatedBy;

    public SystemConfigDO() {
    }

    public SystemConfigDO(String key, String value, String describe) {
        this.key = key;
        this.value = value;
        this.describe = describe;
    }
}