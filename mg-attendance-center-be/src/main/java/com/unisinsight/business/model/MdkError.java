package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "mdk_error")
public class MdkError {
    @Column(name = "id_number")
    private String idNumber;
    private String id;
    private String cause;
    @Column(name = "file_path")
    private String filePath;
    private String type;
    @Column(name = "error_time")
    private Long errorTime;
}
