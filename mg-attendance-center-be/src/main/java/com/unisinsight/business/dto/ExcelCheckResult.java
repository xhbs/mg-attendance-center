package com.unisinsight.business.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelCheckResult<T> {
    private List<T> successList;

    private List<ExcelCheckErrDTO<T>> errorList;

    public ExcelCheckResult(List<T> successDto, List<ExcelCheckErrDTO<T>> errDto) {
        this.successList = successDto;
        this.errorList = errDto;
    }

    public ExcelCheckResult(List<ExcelCheckErrDTO<T>> errDtos) {
        this.successList = new ArrayList<>();
        this.errorList = errDtos;
    }
}
