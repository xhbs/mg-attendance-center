package com.unisinsight.business.model;

import com.unisinsight.business.bo.PartitionRange;
import com.unisinsight.business.common.enums.OriginalRecordSource;
import com.unisinsight.business.common.enums.OriginalRecordStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 原始数据表
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
@Data
@Table(name = "original_records")
public class OriginalRecordDO implements PartitionTable {
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 人员编号
     */
    @Column(name = "person_no")
    private String personNo;

    /**
     * 抓拍照片
     */
    @Column(name = "capture_image")
    private String captureImage;

    /**
     * 来源  {@link OriginalRecordSource}
     */
    @Column(name = "source")
    private Short source;

    /**
     * 设备编码
     */
    @Column(name = "device_code")
    private String deviceCode;

    /**
     * 抓拍时间
     */
    @Column(name = "pass_time")
    private LocalDateTime passTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Override
    public String getTableSuffix() {
        if (passTime == null) {
            return null;
        }
        return PartitionRange.genPartitionRangeByWeek(passTime.toLocalDate()).getTableSuffix();
    }
}