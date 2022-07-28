package com.onenet.exception;

/**
 * Created by zhuocongbin
 * date 2018/3/15
 */
public enum OnenetStatus {
    HTTP_REQUEST_ERROR(1, "http request error"),
    LOAD_CONFIG_ERROR(2, "load config error"),
    WRONG_MESSAGE_ERROR(3, "wrong message error")
    ;
    private String error;
    private int errorNo;
    OnenetStatus(int errorNo, String error) {
        this.error = error;
        this.errorNo = errorNo;
    }
    public String getError() {
        return error;
    }

    public int getErrorNo() {
        return errorNo;
    }
}
