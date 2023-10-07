package com.unisinsight.business.bo;

import lombok.Data;

@Data
public class SubsidStuCountBO {

    /**
     * 名单标识
     */
    private String subListIndex;
    private Long personCount;
    private String desc;
}