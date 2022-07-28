package com.onenet.exception;

/**
 * Created by zhuocongbin
 * date 2018/3/15
 */
public class OnenetException extends RuntimeException {

    private OnenetStatus status;
    private String message = null;
    public OnenetException(OnenetStatus status) {
        this.status = status;
    }
    public OnenetException(OnenetStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    public int getErrorNo() {
        return status.getErrorNo();
    }

    public String getError() {
        if (message != null) {
            return status.getError() + ": " + message;
        } else {
            return status.getError();
        }
    }

    @Override
    public String toString() {
        return "OnenetException{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
