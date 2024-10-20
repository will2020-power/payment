package com.demo.payment.common;

public enum ResultMsgEnum {

    SUCCESS(200, "成功"),
    FAIL(-1, "失败"),
    AUTH_ERROR(502, "授权失败!"),
    SERVER_BUSY(503, "服务器正忙，请稍后再试!");

    private int code;
    private String message;
    ResultMsgEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return this.code;
    }
    public String getMessage() {
        return this.message;
    }



}
