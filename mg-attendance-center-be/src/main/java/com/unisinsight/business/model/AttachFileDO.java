package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 附件文件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
@Data
@Table(name = "attach_files")
public class AttachFileDO {
    /**
     * 记录ID
     */
    @Column(name = "record_id")
    private Integer recordId;

    /**
     * 记录类型 0申诉 1请假 2实习
     */
    @Column(name = "record_type")
    private Integer recordType;

    /**
     * 文件名称
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @Column(name = "file_path")
    private String filePath;
}