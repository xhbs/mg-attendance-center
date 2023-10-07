package com.unisinsight.business.common.utils;

import com.unisinsight.framework.common.exception.BaseErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cn [cheng.nian@unisinsight.com]
 * @desc
 * @time 2020/8/19 10:44
 */
@Data
@NoArgsConstructor
public class Results<T> {
    private static final String SUCCESS_CODE = "0000000000";
    private static final String SUCCESS_MSG = "ok";

    /**
     * 错误代码，成功为0000000000，错误为其他错误码
     */
    @ApiModelProperty(value = "错误代码，0000000000 表示成功")
    private String errorCode;

    /**
     * 错误详情
     */
    @ApiModelProperty(value = "错误消息")
    private String message;

    /**
     * 返回数据
     */
    @ApiModelProperty(value = "业务数据")
    private T data;

    private Results(String errorCode, String message) {
        this(errorCode, message, null);
    }

    private Results(String errorCode, String message, T data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    public static Results<Void> success() {
        return new Results<>(SUCCESS_CODE, SUCCESS_MSG);
    }

    public static <T> Results<T> success(T data) {
        return new Results<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    public static Results<Void> error(String errorCode, String message) {
        return new Results<>(errorCode, message);
    }
    public static <T> Results<T> error(String errorCode, String message,T data) {
        return new Results<>(errorCode, message,data);
    }

    public static Results<Void> error(BaseErrorCode baseErrorCode) {
        return new Results<>(baseErrorCode.getErrorCode(), baseErrorCode.getMessage(), null);
    }

    public static Results<Void> of(BaseErrorCode baseErrorCode) {
        return new Results<>(baseErrorCode.getErrorCode(), baseErrorCode.getMessage(), null);
    }
}
