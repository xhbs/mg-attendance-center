package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.Date;
import java.io.Serializable;

/**
 * 学生照片统计(StutentImageStatistics)实体类
 *
 * @author XieHaiBo
 * @since 2023-03-13 15:15:05
 */
@Data
public class StutentImageStatistics implements Serializable {
    private static final long serialVersionUID = -14312795619001148L;

    private String school;

    private Integer count;

    @Column(name = "update_time")
    private LocalDate updateTime;

}

