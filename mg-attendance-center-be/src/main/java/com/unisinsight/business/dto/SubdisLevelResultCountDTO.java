package com.unisinsight.business.dto;

import lombok.Data;

/**
 * @author tanggang
 * @version 1.0
 *
 * @email tang.gang@inisinsight.com
 * @date 2021/8/16 16:49
 **/
@Data
public class SubdisLevelResultCountDTO {
    private String orgIndex;
    private String orgParentIndex;
    private Integer studentNum;
    private Integer matchPassNum;
    private Integer matchNoPassNum;
}
