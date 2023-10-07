package com.unisinsight.business.dto;

import lombok.Data;

/**
 * 检查失败信息，检查成功信息
 *
 * @param <T>
 */
@Data
public class ExcelCheckErrDTO<T> {
    private T t;

    private String errMsg;

    private Integer rowNum;//当前行号

    public ExcelCheckErrDTO() {
    }

    public ExcelCheckErrDTO(T t, String errMsg, Integer rowNum) {
        this.t = t;
        this.errMsg = errMsg;
        this.rowNum = rowNum;
    }
}
