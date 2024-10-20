package com.demo.payment.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {
    private int code;
    private String message;
    private T data;
    public Result() {
    }
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }
    /**
     * 成功
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(ResultMsgEnum.SUCCESS.getCode());
        result.setMessage(ResultMsgEnum.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }
    /**
     * 失败
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result(code, message);
    }
}
