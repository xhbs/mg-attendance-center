package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 考勤自动布控记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
@Data
@Table(name = "disposition_records")
public class DispositionRecord {
    /**
     * 名单库ID
     */
    @Id
    @Column(name = "tab_id")
    private String tabId;

    /**
     * 布控ID 创建成功后存在
     */
    @Column(name = "disposition_id")
    private String dispositionId;

    /**
     * 是否需要更新 如果考勤设备变更 需要更新
     */
    @Column(name = "need_update")
    private boolean needUpdate;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}
