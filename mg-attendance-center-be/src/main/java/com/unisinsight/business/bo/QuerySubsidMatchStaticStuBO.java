package com.unisinsight.business.bo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
public class QuerySubsidMatchStaticStuBO {
    @Id
    private Long id;

    /**
     * 学生编号
     */
    private String personNo;

    /**
     * 比对状态
     */
    private String status;

    /**
     * 组织标识
     */
    private String orgIndex;


    /**
     * 父组织标识
     */
    private String orgParentIndex;

    /**
     * 正常次数
     */
    private Integer normalNum;

    /**
     * 缺勤次数
     */
    private Integer absentNum;

    /**
     * 名单id
     */
    private Integer subsidRuleId;

    private String searchKey;


}