package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * (TaskResultExpand)实体类
 *
 * @author XieHaiBo
 * @since 2023-03-15 16:03:00
 */

@Data
public class TaskResultExpand implements Serializable {
    private static final long serialVersionUID = -64389902505734718L;

    @Id
    @Column(name = "task_result_id")
    private Long taskResultId;

    @Column
    private String location;

    @Column(name = "place_name")
    private String placeName;

    @Column
    private String picture;

    @Column(name = "match_result")
    private Integer matchResult;

    @Column(name = "absence_reason")
    private String absenceReason;

    @Column(name = "create_time")
    private Long createTime;

}

