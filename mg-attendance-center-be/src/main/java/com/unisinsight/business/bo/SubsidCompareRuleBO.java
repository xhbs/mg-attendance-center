package com.unisinsight.business.bo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;
@Data
public class SubsidCompareRuleBO {

    private Integer id;

    private String subListIndex;

    private Integer absentRate;

    private LocalDate chkDateSt;

    private LocalDate chkDateEd;

    private LocalDate createTimeSt;

    private LocalDate createTimeEd;

    private String orgIndex;

    private List<String> orgIndexs;

    private Short subsidType;

    private Integer rule;

}