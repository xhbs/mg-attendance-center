package com.unisinsight.business.dto;

import lombok.Data;

/**
 * @author tanggang
 * @version 1.0
 * @email tang.gang@inisinsight.com
 * @date 2021/9/3 11:29
 **/
@Data
public class PersonCountDTO {
    /**
     * 组织标识
     */
    private String orgIndex;

    /**
     *学生数量
     */
    private Integer personNum;

}
