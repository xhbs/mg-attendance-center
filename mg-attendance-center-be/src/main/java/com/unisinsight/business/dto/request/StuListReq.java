package com.unisinsight.business.dto.request;

import com.unisinsight.business.bo.PageParam;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StuListReq extends PageParam {
    private Integer taskId;
    private String personNo;
    private LocalDate day;
    private Integer status;
    private Integer result;
}
