package com.unisinsight.business.bo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class QuerySubsidStuResultBO {

    private List<String> personNoList;

    private Integer subsidRuleId;
}
